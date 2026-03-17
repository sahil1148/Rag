package com.sahil.ai.docragengine.controller;


import com.sahil.ai.docragengine.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public String chat(@RequestParam String question) {
        return chatService.ask(question);
    }
}