package com.ecommerce.project.service;

import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, Product product) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));


        product.setCategory(category);
        double specialPrice = product.getPrice() -
                ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        product.setImage("default.png");

        Product savedProduct = productRepository.save(product);
        ProductDTO productDTO;
        productDTO = modelMapper.map(savedProduct, ProductDTO.class);
        return productDTO;
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductDTO> productDTOS;
        productDTOS = products.stream().
                map(product -> modelMapper.map(product,ProductDTO.class))
                                                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {

        /*getting the category*/
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOS;
        productDTOS = products.stream().
                map(product -> modelMapper.map(product,ProductDTO.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();

        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {

        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');

        List<ProductDTO> productDTOS;
        productDTOS = products.stream().
                map(product -> modelMapper.map(product,ProductDTO.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();

        productResponse.setContent(productDTOS);
        return productResponse;

    }
}
