package com.bootcamp.foodorder.service;

import com.bootcamp.foodorder.dto.CreateMenuRequest;
import com.bootcamp.foodorder.dto.MenuPageResponse;
import com.bootcamp.foodorder.dto.MenuResponse;
import com.bootcamp.foodorder.dto.UpdateMenuRequest;
import com.bootcamp.foodorder.entity.Menu;
import com.bootcamp.foodorder.repository.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
public class MenuService {


    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public MenuPageResponse getAllMenus(String name, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Menu> menuPage;

        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (hasName && hasCategory) {
            menuPage = menuRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(
                    name,
                    category,
                    pageable
            );
        } else if (hasName) {
            menuPage = menuRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (hasCategory) {
            menuPage = menuRepository.findByCategoryContainingIgnoreCase(category, pageable);
        } else {
            menuPage = menuRepository.findAll(pageable);
        }

        List<MenuResponse> menuResponses = menuPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return MenuPageResponse.builder()
                .content(menuResponses)
                .page(menuPage.getNumber())
                .size(menuPage.getSize())
                .totalElements(menuPage.getTotalElements())
                .totalPages(menuPage.getTotalPages())
                .last(menuPage.isLast())
                .build();
    }

    public MenuResponse createMenu(CreateMenuRequest request) {
        Menu menu = Menu.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .stock(request.getStock())
                .build();

        Menu savedMenu = menuRepository.save(menu);
        return mapToResponse(savedMenu);
    }

    public MenuResponse updateMenu(Long id, UpdateMenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

        menu.setName(request.getName());
        menu.setDescription(request.getDescription());
        menu.setPrice(request.getPrice());
        menu.setCategory(request.getCategory());
        menu.setStock(request.getStock());

        Menu updatedMenu = menuRepository.save(menu);
        return mapToResponse(updatedMenu);
    }

    public String deleteMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

        menuRepository.delete(menu);
        return "Menu deleted successfully";
    }

    private MenuResponse mapToResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .category(menu.getCategory())
                .stock(menu.getStock())
                .build();
    }
}
