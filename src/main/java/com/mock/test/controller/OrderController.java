package com.mock.test.controller;

import com.github.pagehelper.PageInfo;
import com.mock.test.constant.MallConstant;
import com.mock.test.form.OrderCreateForm;
import com.mock.test.pojo.User;
import com.mock.test.sevice.OrderService;
import com.mock.test.vo.OrderVo;
import com.mock.test.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created by 廖师兄
 */
@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/orders")
	public ResponseVo<OrderVo> create(@Valid @RequestBody OrderCreateForm form,
									  HttpSession session) {
		User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
		return orderService.create(user.getId(), form.getShippingId());
	}

	@GetMapping("/orders")
	public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
									 @RequestParam(defaultValue = "10") Integer pageSize,
									 HttpSession session) {
		User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
		return orderService.list(user.getId(), pageNum, pageSize);
	}

	@GetMapping("/orders/{orderNo}")
	public ResponseVo<OrderVo> detail(@PathVariable Long orderNo,
									  HttpSession session) {
		User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
		return orderService.detail(user.getId(), orderNo);
	}

	@PutMapping("/orders/{orderNo}")
	public ResponseVo cancel(@PathVariable Long orderNo,
							 HttpSession session) {
		User user = (User) session.getAttribute(MallConstant.CURRENT_USER);
		return orderService.cancel(user.getId(), orderNo);
	}

	


}
