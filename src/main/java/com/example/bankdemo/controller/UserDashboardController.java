package com.example.bankdemo.controller;

import com.example.bankdemo.enums.CardType;
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

    /*
    EXPERIMENTAL
     */

    @PostMapping("/cards/create")
    public String createCard(
            @RequestParam String cardName,
            @RequestParam CardType cardType,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            cardService.createForUser(
                    userDetails.getUsername(),
                    cardName,
                    cardType
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Card created successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Card creation failed: " + e.getMessage());
        }

        return "redirect:/user/dashboard";
    }

    @PostMapping("/cards/{id}/rename")
    public String renameCard(
            @PathVariable Long id,
            @RequestParam String cardName,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            cardService.renameCard(
                    id,
                    cardName,
                    userDetails.getUsername()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Card renamed successfully!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not rename card: " + e.getMessage()
            );
        }

        return "redirect:/user/dashboard";
    }

    @PostMapping("/cards/delete/{id}")
    public String deleteCard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            User currentUser =
                    userService.findByUsername(userDetails.getUsername());

            cardService.deleteUserCard(id, currentUser.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Card deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Could not delete card: " + e.getMessage());
        }

        return "redirect:/user/dashboard";
    }

    @PostMapping("/cards/add-balance")
    public String addBalance(
            @RequestParam Long cardId,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            User currentUser =
                    userService.findByUsername(userDetails.getUsername());

            cardService.addBalance(cardId, currentUser.getId(), amount);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Balance added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Could not add balance: " + e.getMessage());
        }

        return "redirect:/user/dashboard";
    }
}