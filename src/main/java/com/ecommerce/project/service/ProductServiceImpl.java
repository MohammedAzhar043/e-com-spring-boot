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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
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
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        /*mapping*/
        Product product = modelMapper.map(productDTO, Product.class);

        product.setCategory(category);
        double specialPrice = product.getPrice() -
                ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        product.setImage("default.png");

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
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

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        //get the existing product
        Product productFromDb = productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

        /*mapping*/
        Product product = modelMapper.map(productDTO, Product.class);

        //update the product info with user shared
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        double specialPrice = product.getPrice() -
                ((product.getDiscount() * 0.01) * product.getPrice());
        productFromDb.setSpecialPrice(specialPrice);

        //save to data base
        Product savedProduct = productRepository.save(productFromDb);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product productfromDb = productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(productfromDb);
        return modelMapper.map(productfromDb,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        //Get the product form DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId", productId));
        //upload the image to server
        //get the file name of uploaded image

        String path ="images/";
        String filename = uploadImage(path,image);
        //updating the new file name to the product
        productFromDb.setImage(filename);
        //save the update product
        Product updatedProduct = productRepository.save(productFromDb);
        //return Dto after mapping product to productDTO
        return  modelMapper.map(updatedProduct,ProductDTO.class);

    }

    private String uploadImage(String path, MultipartFile file) throws IOException {

        //File names of the current file/original file
        String orignalFileName = file.getOriginalFilename();
        //renaming the file / generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(orignalFileName.substring(orignalFileName.lastIndexOf('.')));

        String filePath = path + File.separator + fileName;
        //check if path exist and create

        File folder = new File(path);
       if(!folder.exists()){
           folder.mkdirs();
       }
        //upload to server

        Files.copy(file.getInputStream(), Paths.get(filePath));

        //returning file name
        return fileName;
    }
}
