package com.dicoding.smartreply

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplyGenerator
import com.google.mlkit.nl.smartreply.SmartReplySuggestion
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage

class ChatViewModel : ViewModel() {

    private val anotherUserID = "101"

    private val _chatHistory = MutableLiveData<ArrayList<Message>>()
    val chatHistory: LiveData<ArrayList<Message>> = _chatHistory

    private val _pretendingAsAnotherUser = MutableLiveData<Boolean>()
    val pretendingAsAnotherUser: LiveData<Boolean> = _pretendingAsAnotherUser

    private val smartReply: SmartReplyGenerator = SmartReply.getClient()

    private val _smartReplyOptions = MediatorLiveData<List<SmartReplySuggestion>>()
    val smartReplyOptions: LiveData<List<SmartReplySuggestion>> = _smartReplyOptions

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        initSmartReplyOptionsGenerator()
        _pretendingAsAnotherUser.value = false
    }

    fun switchUser() {
        clearSmartReplyOptions()
        val value = _pretendingAsAnotherUser.value!!
        _pretendingAsAnotherUser.value = !value
    }

    fun setMessages(messages: ArrayList<Message>) {
        clearSmartReplyOptions()
        _chatHistory.value = messages
    }

    fun addMessage(message: String){

        val user = _pretendingAsAnotherUser.value!!

        var list: ArrayList<Message> = chatHistory.value ?: ArrayList()
        list.add(Message(message, !user, System.currentTimeMillis()))

        clearSmartReplyOptions()

        _chatHistory.value = list

    }

    private fun initSmartReplyOptionsGenerator() {
        _smartReplyOptions.addSource(pretendingAsAnotherUser) { isPretendingAsAnotherUser ->
            val list = chatHistory.value

            if (list.isNullOrEmpty()) {
                _smartReplyOptions.value = emptyList()
                return@addSource
            } else {
                generateSmartReplyOptions(list, isPretendingAsAnotherUser)
                    .addOnSuccessListener { result ->
                        _smartReplyOptions.value = result
                    }
            }
        }

        _smartReplyOptions.addSource(chatHistory) { conversations ->
            val isPretendingAsAnotherUser = pretendingAsAnotherUser.value

            if (isPretendingAsAnotherUser != null && conversations.isNullOrEmpty()) {
                _smartReplyOptions.value = emptyList()
                return@addSource
            } else {
                generateSmartReplyOptions(conversations, isPretendingAsAnotherUser!!)
                    .addOnSuccessListener { result ->
                        _smartReplyOptions.value = result
                    }
            }
        }
    }

    private fun generateSmartReplyOptions(
        messages: List<Message>,
        isPretendingAsAnotherUser: Boolean
    ): Task<List<SmartReplySuggestion>> {
        val lastMessage = messages.last()

        // Pastikan pesan terakhir berasal dari pengguna yang berbeda dari yang sedang berpura-pura
        if (lastMessage.isLocalUser != isPretendingAsAnotherUser) {
            return Tasks.forException(Exception("Tidak menjalankan smart reply!"))
        }

        // Inisialisasi daftar percakapan untuk smart reply
        val chatConversations = ArrayList<TextMessage>()
        for (message in messages) {
            if (message.isLocalUser != isPretendingAsAnotherUser) {
                // Jika pesan berasal dari pengguna lokal, tambahkan ke daftar percakapan lokal
                chatConversations.add(
                    TextMessage.createForLocalUser(
                        message.text,
                        message.timestamp
                    )
                )
            } else {
                // Jika pesan berasal dari pengguna remote, tambahkan ke daftar percakapan remote
                chatConversations.add(
                    TextMessage.createForRemoteUser(
                        message.text,
                        message.timestamp,
                        anotherUserID
                    )
                )
            }
        }

        // Cek apakah daftar percakapan tidak kosong
        if (chatConversations.isEmpty()) {
            return Tasks.forException(IllegalArgumentException("Daftar pesan tidak boleh kosong"))
        }

        // Gunakan smart reply untuk mendapatkan saran balasan
        return smartReply
            .suggestReplies(chatConversations)
            .continueWith { task ->
                val result = task.result
                when (result.status) {
                    SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE ->
                        _errorMessage.value =
                            "Unable to generate options due to a non-English language was used"

                    SmartReplySuggestionResult.STATUS_NO_REPLY ->
                        _errorMessage.value =
                            "Unable to generate options due to no appropriate response found"
                }
                result.suggestions
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "An error has occurred on Smart Reply Instance: ${e.message}"
            }
    }

    private fun clearSmartReplyOptions(){
        _smartReplyOptions.value = ArrayList()
    }

    override fun onCleared() {
        super.onCleared()
        smartReply.close()
    }

    companion object {
        private const val TAG = "ChatViewModel"
    }

}
