package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserService;

@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	public PurchaseController() {
		System.out.println("PurchaseController default constructor call");
	}
	
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	@RequestMapping(value="/addPurchase",method=RequestMethod.GET)
	public ModelAndView addPurchaseView( @RequestParam("prod_no") int prodNo) throws Exception {

		System.out.println("/addPurchaseView.do");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/addPurchaseView.jsp");
		modelAndView.addObject("purchaseProd", productService.getProduct(prodNo));
		
		return modelAndView;
	}
	
	@RequestMapping(value="/addPurchase",method=RequestMethod.POST)
	public ModelAndView addPurchase( @RequestParam("prodNo") int prodNo, @RequestParam("buyerId") String buyerId,
										@ModelAttribute("purchase") Purchase purchase ) throws Exception {

		System.out.println("/addPurchase.do");
		
		purchase.setPurchaseProd(productService.getProduct(prodNo));
		purchase.setBuyer(userService.getUser(buyerId));
		purchase.setTranCode("1");
		
		purchaseService.addPurchase(purchase);
		
		purchase = purchaseService.getPurchase2(prodNo);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/confirmPurchase.jsp");
		
		return modelAndView;
	}
	
	@RequestMapping(value="/getPurchase",method=RequestMethod.GET)
	public ModelAndView getPurchase( @RequestParam("tranNo") int tranNo ) throws Exception {
		
		System.out.println("/getPurchase.do");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/getPurchase.jsp");
		modelAndView.addObject("purchase",purchaseService.getPurchase(tranNo));
		
		return modelAndView;
	}
		
	@RequestMapping(value="/listPurchase",method=RequestMethod.GET)
	public ModelAndView listPurchase( @ModelAttribute("search") Search search , Model model , HttpSession session) throws Exception{
		
		System.out.println("/listPurchase.do");
		
		if(	search.getCurrentPage()==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String , Object> map=purchaseService.getPurchaseList(search,((User)session.getAttribute("user")).getUserId());
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/listPurchase.jsp");
		modelAndView.addObject("list", map.get("list"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		
		return modelAndView;
	}
	
	@RequestMapping(value="/updatePurchase",method=RequestMethod.GET)
	public ModelAndView updatePurchaseView( @RequestParam("tranNo") int tranNo ) throws Exception{

		System.out.println("/updatePurchaseView.do");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/updatePurchaseView.jsp");
		modelAndView.addObject("purchase", purchaseService.getPurchase(tranNo));
				
		return modelAndView;
	}
	
	@RequestMapping(value="/updatePurchase",method=RequestMethod.POST)
	public ModelAndView updatePurchase( @RequestParam("tranNo") int tranNo, @ModelAttribute("purchase") Purchase purchase ) throws Exception{

		System.out.println("/updatePurchase.do");
		purchase.setTranNo(tranNo);
		purchaseService.updatePurchase(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/purchase/getPurchase?tranNo="+tranNo);
		
		
		return modelAndView;
	}
	
	@RequestMapping(value="/updateTranCode",method=RequestMethod.GET)
	public ModelAndView updateTranCode( @RequestParam("tranNo") int tranNo, @RequestParam("tranCode") String tranCode ) throws Exception{

		System.out.println("/updateTranCode.do");
		Purchase purchase = purchaseService.getPurchase(tranNo);
		purchase.setTranCode(tranCode);
		purchaseService.updateTranCode(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/purchase/listPurchase");
		
		return modelAndView;
	}
	
	@RequestMapping(value="/updateTranCodeByProd",method=RequestMethod.GET)
	public ModelAndView updateTranCodeByProd( @RequestParam("prodNo") int prodNo, @RequestParam("tranCode") String tranCode ) throws Exception{

		System.out.println("/updateTranCodeByProd.do");
		Purchase purchase = purchaseService.getPurchase2(prodNo);
		purchase.setTranCode(tranCode);
		purchaseService.updateTranCode(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/product/listProduct?menu=manage");
		
		return modelAndView;
	}
	
}
