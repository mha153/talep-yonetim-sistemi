package com.talep.base;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/talepler")
public class TalepController {

    private final TalepRepository talepRepository;

    public TalepController(TalepRepository talepRepository) {
        this.talepRepository = talepRepository;
    }

    @GetMapping
    public List<Talep> tumTalepleriGetir() {
        return talepRepository.findAll();
    }
}