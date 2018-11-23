package com.model2.mvc.web.product;

import java.io.File;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;

@RestController
@RequestMapping("/product/*")
public class ProductRestController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
		
	public ProductRestController(){
		System.out.println(this.getClass());
	}
	
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	@RequestMapping( value="json/addProduct", method=RequestMethod.POST )
	public Product addProduct( @RequestBody Product product) throws Exception {
		
		System.out.println("/addProduct.do");
		
		System.out.println(product);
		productService.addProduct(product);
		
		return product;
	}
	
	@RequestMapping( value="json/getProduct/{prodNo}/{menu}", method=RequestMethod.GET )
	public Product getProduct( @PathVariable int prodNo , @PathVariable String menu, 
								@CookieValue(value="history", required=false) String history, HttpServletResponse response) throws Exception {
		
		System.out.println("/getProduct.do");
		Product product = productService.getProduct(prodNo);
		
		//열어본 상품
		if (history == null || history.length()==0) {
			history=prodNo+"";
		} else {
			if (history.indexOf(prodNo+"") != -1) {
				history=history.replace(prodNo+",", "");
				history=history.replace(","+prodNo, "");//마지막에 붙어있는 거 없애기
				history=prodNo+","+history;
			} else {
				history=prodNo+","+history;
			}
		}
		Cookie cookie = new Cookie("history",history);
		cookie.setMaxAge(-1);
		cookie.setPath("/");
		response.addCookie(cookie);		
		
		return product;
	}
	
	@RequestMapping(value="json/updateProduct/{prodNo}", method=RequestMethod.GET)
	public Product updateProduct( @PathVariable("prodNo") int prodNo) throws Exception{

		System.out.println("/updateProductView.do");
		Product product=productService.getProduct(prodNo);
		/*model.addAttribute("product", product);*/
		
		return product;
	}
	
	@RequestMapping(value="json/updateProduct", method=RequestMethod.POST)
	public Product updateProduct( @RequestBody Product product) throws Exception{

		System.out.println("/updateProduct.do");
		productService.updateProduct(product);
		product = productService.getProduct(product.getProdNo());
		
		return product;
	}

	@RequestMapping("json/listProduct")
	public Map<String, Object> listProduct( @RequestBody (required=false) Search search) throws Exception{
		if (search == null) {
			System.out.println("null");
			search=new Search();
		}
		
		System.out.println("/listProduct.do");
		
		if(	search.getCurrentPage()==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		System.out.println("search: "+search);
				
		Map<String , Object> map=productService.getProductList(search);
		System.out.println("map:"+map);
		
		return map;
	}
}