package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.AddressBook;
import com.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 获取所有的地址
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @Transactional
    public R<AddressBook> defaultAddress(@RequestBody AddressBook addressBook) {

        AddressBook defaultAddr = addressBookService.setDefaultAddr(addressBook);

        return R.success(defaultAddr);
    }

    /**
     * 添加地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {

        //这里除了用session获取id，还可以使用BaseContext.getId()（在过滤器确定登录时已经设置了）
        addressBook.setUserId(BaseContext.getId());
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }

    /**
     * 回显地址，根据id查找对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable("id") Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        }
        return R.error("没有找到该对象");
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("更改地址车成功");
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        addressBookService.removeById(id);
        return R.success("删除成功");
    }

    /**
     * 查询用户默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getId());

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook != null) {
            return R.success(addressBook);
        }else {
            return R.error("没有找到该对象");
        }
    }

}
