package com.model2.mvc.web.purchase;

import java.io.File;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserService;

@RestController
@RequestMapping("/purchase/*")
public class PurchaseRestController {
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
		
	public PurchaseRestController(){
		System.out.println(this.getClass());
	}
	
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	@RequestMapping( value="json/addPurchase/{prodNo}", method=RequestMethod.GET )
	public Product addPurchase( @PathVariable int prodNo ) throws Exception {
		
		System.out.println("/addPurchase.do");
		
		Product product = productService.getProduct(prodNo);
		
		return product;
	}
	
	@RequestMapping( value="json/addPurchase", method=RequestMethod.POST )
	public Purchase addProduct( @RequestBody String str ) throws NumberFormatException, Exception {
		
		System.out.println("/addPurchase.do POST");
		System.out.println("map: "+str);
		
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("hello");
		
		JsonNode node = mapper.readTree(str);
		
		System.out.println("node: "+node.get("purchase"));
		
		Purchase purchase = mapper.convertValue(node.get("purchase"), Purchase.class);
		purchase.setPurchaseProd(productService.getProduct(Integer.parseInt(node.get("prodNo").toString())));
		purchase.setBuyer(userService.getUser(node.get("buyerId").asText()));
		System.out.println("purchase: "+purchase);
		purchaseService.addPurchase(purchase);
		
		purchase = purchaseService.getPurchase2(Integer.parseInt(node.get("prodNo").toString()));
				
		return purchase;
	}
	@RequestMapping(value="json/getPurchase/{tranNo}",method=RequestMethod.GET)
	public Purchase getPurchase( @PathVariable int tranNo ) throws Exception {
		
		System.out.println("/getPurchase.do");
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		return purchase;
	}
	
	@RequestMapping(value="json/listPurchase")
	public Map<String , Object> listPurchase(  @RequestBody (required=false) Search search, HttpSession session) throws Exception{
		if (search == null) {
			System.out.println("null");
			search=new Search();
		}
		
		System.out.println("/listPurchase.do");
		
		if(	search.getCurrentPage()==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String , Object> map=purchaseService.getPurchaseList(search,"user01");
		System.out.println("map: "+map);	
			
		return map;
	}
		
	@RequestMapping(value="json/updatePurchase/{tranNo}", method=RequestMethod.GET)
	public Purchase updatePurchase( @PathVariable int tranNo ) throws Exception{

		System.out.println("/updatePurchaseView.do");
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		return purchase;
	}
	
	@RequestMapping(value="json/updatePurchase", method=RequestMethod.POST)
	public Purchase updateProduct( @RequestBody String str ) throws Exception{

		System.out.println("/updatePurchase.do POST");
		System.out.println("map: "+str);
		
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode node = mapper.readTree(str);
				
		Purchase purchase = mapper.convertValue(node.get("purchase"), Purchase.class);
		purchase.setTranNo(Integer.parseInt(node.get("tranNo").toString()));
		System.out.println("purchase: "+purchase);
		purchaseService.updatePurchase(purchase);
		purchase = purchaseService.getPurchase(Integer.parseInt(node.get("tranNo").toString()));
		
		return purchase;
	}
	
	@RequestMapping(value="json/updateTranCode/{tranNo}/{tranCode}",method=RequestMethod.GET)
	public Purchase updateTranCode( @PathVariable int tranNo, @PathVariable String tranCode ) throws Exception{

		System.out.println("/updateTranCode.do");
		Purchase purchase = purchaseService.getPurchase(tranNo);
		purchase.setTranCode(tranCode);
		purchaseService.updateTranCode(purchase);
		
		purchase = purchaseService.getPurchase(tranNo);
		
		return purchase;
	}
	
	@RequestMapping(value="json/updateTranCodeByProd/{prodNo}/{tranCode}",method=RequestMethod.GET)
	public Purchase updateTranCodeByProd( @PathVariable int prodNo, @PathVariable String tranCode ) throws Exception{

		System.out.println("/updateTranCodeByProd.do");
		Purchase purchase = purchaseService.getPurchase2(prodNo);
		purchase.setTranCode(tranCode);
		purchaseService.updateTranCode(purchase);
		
		purchase = purchaseService.getPurchase2(prodNo);
		
		return purchase;
	}
}