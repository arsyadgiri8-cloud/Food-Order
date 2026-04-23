package com.bootcamp.foodorder.controller;

import com.bootcamp.foodorder.dto.CreateMenuRequest;
import com.bootcamp.foodorder.dto.MenuPageResponse;
import com.bootcamp.foodorder.dto.MenuResponse;
import com.bootcamp.foodorder.dto.UpdateMenuRequest;
import com.bootcamp.foodorder.service.MenuService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/menus")
@Tag(name = "Menu", description = "Menu management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<MenuPageResponse> getAllMenus(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        MenuPageResponse response = menuService.getAllMenus(name, category, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        MenuResponse response = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuRequest request
    ) {
        MenuResponse response = menuService.updateMenu(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenu(@PathVariable Long id) {
        String response = menuService.deleteMenu(id);
        return ResponseEntity.ok(response);
    }
}
