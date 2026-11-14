package com.example.bankdemo.controller;

import com.example.bankdemo.model.User;
import com.example.bankdemo.model.Card;
import com.example.bankdemo.service.CardService;
import com.example.bankdemo.service.TransferService;
import com.example.bankdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserDashboardController {

    private final UserService userService;
    private final TransferService transferService;
    private final CardService cardService;

    @GetMapping("/dashboard")
    public String getUserDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        List<Card> userCards = cardService.getCardsByUserId(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("cards", userCards);
        model.addAttribute("transactions", transferService.getRecentTransactionsForUser(currentUser.getId()));

        return "user/dashboard";
    }

    @PostMapping("/transfer")
    public String processTransfer(@RequestParam("fromCardId") Long fromCardId,
                                  @RequestParam("toCardId") Long toCardId,
                                  @RequestParam("amount") BigDecimal amount,
                                  RedirectAttributes redirectAttributes) {
        try {
            transferService.transfer(fromCardId, toCardId, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Transfer successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Transfer failed: " + e.getMessage());
        }

        return "redirect:/user/dashboard";
    }
}