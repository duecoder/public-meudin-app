package br.com.due.meudin.controller;

import br.com.due.meudin.domain.info.SpendCategory;
import br.com.due.meudin.domain.user.CustomUserDetails;
import br.com.due.meudin.dto.category.NewCategoryDTO;
import br.com.due.meudin.dto.spend.SpendCategoryDTO;
import br.com.due.meudin.repository.SpendCategoryRepository;
import br.com.due.meudin.repository.SpendRepository;
import br.com.due.meudin.service.spendCategory.SpendCategoryService;
import br.com.due.meudin.util.ResponseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class SpendCategoryController {
    @Autowired
    SpendCategoryRepository categoryRepository;
    @Autowired
    SpendCategoryService categoryService;
    @Autowired
    SpendRepository spendRepository;

    @GetMapping
    public List<SpendCategoryDTO> getList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<SpendCategory> categories = categoryRepository.findByUserId(userId);
        return categories.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @GetMapping("/categoryCheck/{categoryId}")
    public ResponseEntity<Boolean> existsSpendByUserAndCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long categoryId) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(spendRepository.existsByUserIdAndCategoryId(userId, categoryId));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseJson> registerCategory(@RequestBody NewCategoryDTO dto) {
        ResponseJson response = categoryService.addCategory(dto.userId(), dto.description());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ResponseJson> deleteCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long categoryId) {
        ResponseJson response = categoryService.deleteCategory(categoryId, userDetails.getUserId());
        return ResponseEntity.ok().body(response);
    }

    private SpendCategoryDTO mapToDTO(SpendCategory category) {
        SpendCategoryDTO dto = new SpendCategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setDescription(category.getDescription());
        dto.setUserId(category.getUserId());
        dto.setUserDefault(category.isUserDefault());

        return dto;
    }
}
