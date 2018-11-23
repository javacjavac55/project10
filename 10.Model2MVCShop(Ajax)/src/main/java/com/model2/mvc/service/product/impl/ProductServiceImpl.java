package com.model2.mvc.service.product.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductDao;
import com.model2.mvc.service.product.ProductService;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	@Qualifier("productDaoImpl")
	private ProductDao productDao;
	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}
	
	public ProductServiceImpl() {
		
	}

	@Override
	public int addProduct(Product product) throws Exception {
		return productDao.addProduct(product);
	}

	@Override
	public Product getProduct(int prodNo) throws Exception {
		return productDao.getProduct(prodNo);
	}

	@Override
	public Map<String, Object> getProductList(Search search) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		
		map.put("totalCount", productDao.getTotalCount(search));
		map.put("list", productDao.getProductList(search));
		return map;
	}

	@Override
	public int updateProduct(Product product) throws Exception {
		return productDao.updateProduct(product);
	}

	@Override
	public List<Product> getRandomList(int randomSize) throws Exception {
		return productDao.getRandomList(randomSize);
	}

}
