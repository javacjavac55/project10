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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;

@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
		
	public ProductController(){
		System.out.println(this.getClass());
	}
	
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	@RequestMapping( value="/addProduct", method=RequestMethod.POST )
	public String addProduct( @ModelAttribute("product") Product product, @RequestParam("uploadFile") MultipartFile multipartFile ) throws Exception {
		System.out.println("/addProduct.do");
		
		String fileName = System.currentTimeMillis()+"."+multipartFile.getOriginalFilename().split("\\.")[1];
		String path = "C:\\workspace\\10.Model2MVCShop(Ajax)\\WebContent\\images\\uploadFiles\\";
		
		File file = new File(path+fileName);
		multipartFile.transferTo(file);
		
		product.setFileName(fileName);
		productService.addProduct(product);
		
		return "forward:/product/confirmProduct.jsp";
	}
	
	@RequestMapping( value="/getProduct", method=RequestMethod.GET )
	public String getProduct( @RequestParam("prodNo") int prodNo , @RequestParam("menu") String menu, 
								@CookieValue(value="history", required=false) String history, HttpServletResponse response, Model model ) throws Exception {
		
		System.out.println("/getProduct.do");
		Product product = productService.getProduct(prodNo);
		model.addAttribute("product", product);
		
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
		
		return "forward:/product/getProduct.jsp";
	}
	
	@RequestMapping(value="/updateProduct", method=RequestMethod.GET)
	public String updateProduct( @RequestParam("prodNo") int prodNo , Model model ) throws Exception{

		System.out.println("/updateProductView.do");
		Product product=productService.getProduct(prodNo);
		model.addAttribute("product", product);
		
		return "forward:/product/updateProductView.jsp";
	}
	
	@RequestMapping(value="/updateProduct", method=RequestMethod.POST)
	public String updateProduct(  @ModelAttribute("product") Product product, @RequestParam(value="uploadFile", required=false) MultipartFile multipartFile ) throws Exception{
		System.out.println("/updateProduct.do");
		
		if (multipartFile!=null && !multipartFile.isEmpty()) {
			String fileName = String.valueOf(System.currentTimeMillis())+"."+multipartFile.getOriginalFilename().split("\\.")[1];
			String path = "C:\\workspace\\10.Model2MVCShop(Ajax)\\WebContent\\images\\uploadFiles\\";
			
			File file = new File(path+fileName);
			multipartFile.transferTo(file);
			
			product.setFileName(fileName);
		}
		productService.updateProduct(product);
		
		return "redirect:/product/getProduct?prodNo="+product.getProdNo()+"&menu=manage";
	}

	@RequestMapping("/listProduct")
	public String listProduct( @ModelAttribute("search") Search search , Model model , HttpSession session) throws Exception{
		
		System.out.println("/listProduct.do");
		
		if(	search.getCurrentPage()==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String , Object> map=productService.getProductList(search);
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		String dest = "forward:/product/listProductUser.jsp";
		if (session.getAttribute("user")!=null && ((User)session.getAttribute("user")).getRole().equals("admin"))
			dest = "forward:/product/listProduct.jsp";
		
		return dest;
	}
}