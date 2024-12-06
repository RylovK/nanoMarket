package my.project.productservice.controller;

import lombok.RequiredArgsConstructor;
import my.project.productservice.service.CategoryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
}
