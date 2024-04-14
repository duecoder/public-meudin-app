package br.com.due.meudin.service.spendCategory;

import br.com.due.meudin.domain.info.SpendCategory;
import br.com.due.meudin.domain.spend.Spend;
import br.com.due.meudin.exception.APIDefaultError;
import br.com.due.meudin.repository.SpendCategoryRepository;
import br.com.due.meudin.repository.SpendRepository;
import br.com.due.meudin.util.ResponseJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class SpendCategoryService {
    @Autowired
    SpendCategoryRepository categoryRepository;
    @Autowired
    SpendRepository spendRepository;

    public ResponseJson addCategory(Long userId, String category) {
        ResponseJson response = new ResponseJson(false, "Something went wrong");
        try {
            SpendCategory newCategory = new SpendCategory();
            newCategory.setDescription(category);
            newCategory.setUserId(userId);
            categoryRepository.save(newCategory);

            response.setSuccess(true);
            response.setMessage("Category added successfully!");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
        return response;
    }

    public ResponseJson deleteCategory(Long id, Long userId) {
        ResponseJson response = new ResponseJson(false, "Something went wrong");
        SpendCategory category = null;
        SpendCategory defaultCategory = categoryRepository.findUserDefaultByUserId(userId);
        try {
            Optional<SpendCategory> categoryOp = categoryRepository.findById(id);
            boolean isDefaultCategory = false;
            if (categoryOp.isPresent()) {
                category = categoryOp.get();
                isDefaultCategory = category.isUserDefault();
            }
            if (isDefaultCategory) {
                // Bloqueamos o usuário de excluir a categoria default
                response.setMessage("You can't delete the Default category");
            } else {
                // Antes de excluir a categoria, vou alterar todas as spends desse usuário com a categoria
                // a ser deletada, para a categoria default
                spendRepository
                .setUserSpendsToDefaultCategory(
                        userId,
                        defaultCategory.getCategoryId(),
                        category.getCategoryId());

                categoryRepository.delete(category);
                response.setSuccess(true);
                response.setMessage("Category deleted successfully");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIDefaultError();
        }
        return response;
    }
}
