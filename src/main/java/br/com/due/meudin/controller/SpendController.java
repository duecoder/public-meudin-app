package br.com.due.meudin.controller;

import br.com.due.meudin.domain.spend.Spend;
import br.com.due.meudin.domain.user.CustomUserDetails;
import br.com.due.meudin.dto.spend.HomeSpendsDTO;
import br.com.due.meudin.dto.spend.SpendChartDTO;
import br.com.due.meudin.dto.spend.SpendDTO;
import br.com.due.meudin.repository.SpendRepository;
import br.com.due.meudin.service.spend.SpendService;
import br.com.due.meudin.util.GlobalMethods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/spends")
@CrossOrigin(origins = "*")
public class SpendController {
    @Autowired
    SpendRepository repository;
    @Autowired
    SpendService service;

    @GetMapping
    public List<SpendDTO> getList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<Spend> spends = repository.findByUserId(userId);
        return spends.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @GetMapping("/home")
    public HomeSpendsDTO getHomeSpendsData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.getHomeSpendsData(userDetails.getUserId());
    }

    @GetMapping("/chart/{date}")
    public List<SpendChartDTO> getChartdata(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String date) {
        return service.getChartData(userDetails.getUserId(), date);
    }

    @PostMapping("/save")
    public ResponseEntity saveSpend(@RequestBody Spend spend) {
        return ResponseEntity.ok().body(service.save(spend));
    }

    @PutMapping("/edit")
    public ResponseEntity updateSpend(@RequestBody Spend spend) {
        return ResponseEntity.ok().body(service.save(spend));
    }

    @DeleteMapping("/{id}")
    public void deleteSpend(@PathVariable Long id) {
        service.deleteSpend(id);
    }

    @DeleteMapping("/many")
    public void deleteSpend(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody String ids) {
        List<Long> idList = GlobalMethods.convertStringToLongList(ids);
        Long userId = userDetails.getUserId();
        service.deleteManySpends(userId, idList);
    }

    private SpendDTO mapToDTO(Spend spend) {
        SpendDTO dto = new SpendDTO();
        dto.setId(spend.getId());
        dto.setNature(spend.getNature());
        dto.setDescription(spend.getDescription());
        dto.setCategory(spend.getCategory());
        dto.setCost(spend.getCost());
        dto.setDate(GlobalMethods.formatDate(spend.getDate(), "dd/MM/yyyy"));
        dto.setCardId(spend.getCardId());
        dto.setPaid(spend.getPaid());
        dto.setInvoiceId(spend.getInvoiceId());

        return dto;
    }
}
