package com.ecommerce.project.service;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {


        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        /*mapping*/
        Product product = modelMapper.map(productDTO, Product.class);

        //check if product already present or not

        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent){
            product.setCategory(category);
            double specialPrice = product.getPrice() -
                    ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            product.setImage("default.png");

            Product savedProduct = productRepository.save(product);

            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else {
            throw new APIException("Product already exist!!");
        }


    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {


        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> allProducts = productPage.getContent();

        //product size is zero or not
        if(allProducts.isEmpty()) {
            throw new APIException("No products found, Please try to add a product.");
        }

        List<ProductDTO> productDTOS;
        productDTOS = allProducts.stream().
                map(product -> modelMapper.map(product,ProductDTO.class))
                                                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPage(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        /*getting the category*/
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products = productPage.getContent();

        //product size is zero or not
        if(products.isEmpty()) {
            throw new APIException("No products found for the categoryId "+categoryId+", Please try to add a product.");
        }
        else{
            List<ProductDTO> productDTOS;
            productDTOS = products.stream().
                    map(product -> modelMapper.map(product,ProductDTO.class))
                    .collect(Collectors.toList());
            ProductResponse productResponse = new ProductResponse();

            productResponse.setContent(productDTOS);
            productResponse.setPageNumber(productPage.getNumber());
            productResponse.setPageSize(productPage.getSize());
            productResponse.setTotalElements(productPage.getTotalElements());
            productResponse.setTotalPage(productPage.getTotalPages());
            productResponse.setLastPage(productPage.isLast());
            return productResponse;

        }


    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {


        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageDetails);
        List<Product> products = productPage.getContent();

        //product size is zero or not
        if(products.isEmpty()) {
            throw new APIException("No products found with keyword "+ keyword + " , Please try to add a product.");
        }
        else {
            List<ProductDTO> productDTOS;
            productDTOS = products.stream().
                    map(product -> modelMapper.map(product,ProductDTO.class))
                    .collect(Collectors.toList());
            ProductResponse productResponse = new ProductResponse();

            productResponse.setContent(productDTOS);
            productResponse.setPageNumber(productPage.getNumber());
            productResponse.setPageSize(productPage.getSize());
            productResponse.setTotalElements(productPage.getTotalElements());
            productResponse.setTotalPage(productPage.getTotalPages());
            productResponse.setLastPage(productPage.isLast());
            return productResponse;

        }



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

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product productfromDb = productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // DELETE the product from the cart
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

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


        String filename = fileService.uploadImage(path,image);
        //updating the new file name to the product
        productFromDb.setImage(filename);
        //save the update product
        Product updatedProduct = productRepository.save(productFromDb);
        //return Dto after mapping product to productDTO
        return  modelMapper.map(updatedProduct,ProductDTO.class);

    }


}
