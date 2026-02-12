package com.genixo.education.search.service.supply;

import com.genixo.education.search.common.exception.BusinessException;
import com.genixo.education.search.common.exception.ResourceNotFoundException;
import com.genixo.education.search.dto.supply.MessageCreateDto;
import com.genixo.education.search.dto.supply.MessageDto;
import com.genixo.education.search.entity.supply.ProductConversation;
import com.genixo.education.search.entity.supply.ProductMessage;
import com.genixo.education.search.entity.user.User;
import com.genixo.education.search.repository.user.UserRepository;
import com.genixo.education.search.repository.supply.ProductConversationRepository;
import com.genixo.education.search.repository.supply.ProductMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MessageService {

    private final ProductMessageRepository messageRepository;
    private final ProductConversationRepository conversationRepository;
    private final UserRepository userRepository;

    // ================================ MESSAGE CRUD ================================

    @Transactional
    public MessageDto sendMessage(Long conversationId, Long userId, MessageCreateDto createDto) {
        log.info("Sending message to conversation ID: {} by user ID: {}", conversationId, userId);

        // Validate conversation
        ProductConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId));

        // Validate user
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));


        // Create message
        ProductMessage message = new ProductMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(createDto.getContent());
        message.setAttachmentUrl(createDto.getAttachmentUrl());
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        ProductMessage saved = messageRepository.save(message);

        // Update conversation updatedAt
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        log.info("Message sent successfully with ID: {}", saved.getId());
        return mapToDto(saved);
    }

    public Page<MessageDto> getConversationMessages(Long conversationId, Pageable pageable) {
        log.info("Fetching messages for conversation ID: {}", conversationId);

        if (!conversationRepository.existsById(conversationId)) {
            throw new ResourceNotFoundException("Conversation", conversationId);
        }

        Page<ProductMessage> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);
        return messages.map(this::mapToDto);
    }

    public MessageDto getMessageById(Long id) {
        log.info("Fetching message with ID: {}", id);

        ProductMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", id));

        return mapToDto(message);
    }

    @Transactional
    public MessageDto markMessageAsRead(Long id, Long userId) {
        log.info("Marking message ID: {} as read by user ID: {}", id, userId);

        ProductMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", id));

        // Only mark as read if user is not the sender
        if (!message.getSender().getId().equals(userId)) {
            message.setIsRead(true);
            messageRepository.save(message);
        }

        return mapToDto(message);
    }

    @Transactional
    public void markAllMessagesAsRead(Long conversationId, Long userId) {
        log.info("Marking all messages as read for conversation ID: {} by user ID: {}", conversationId, userId);

        if (!conversationRepository.existsById(conversationId)) {
            throw new ResourceNotFoundException("Conversation", conversationId);
        }

        messageRepository.markAllAsReadByConversationIdAndUserId(conversationId, userId);
        log.info("All messages marked as read for conversation ID: {}", conversationId);
    }

    @Transactional
    public void deleteMessage(Long id, Long userId) {
        log.info("Deleting message ID: {} by user ID: {}", id, userId);

        ProductMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", id));

        // Only sender can delete their own message
        if (!message.getSender().getId().equals(userId)) {
            throw new BusinessException("You can only delete your own messages");
        }

        messageRepository.delete(message);
        log.info("Message deleted successfully with ID: {}", id);
    }

    // ================================ HELPER METHODS ================================

    private MessageDto mapToDto(ProductMessage message) {
        User sender = message.getSender();

        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(sender.getId())
                .senderName(sender.getFirstName() + " " + sender.getLastName())
                .senderEmail(sender.getEmail())
                .content(message.getContent())
                .attachmentUrl(message.getAttachmentUrl())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

