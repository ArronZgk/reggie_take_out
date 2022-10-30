package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.entity.AddressBook;
import com.reggie.mapper.AddressBookMapper;
import com.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @Override
    @Transactional
    public AddressBook setDefaultAddr(AddressBook addressBook) {

        //将所有地址信息的status都设置为0
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AddressBook::getIsDefault, 0).eq(AddressBook::getUserId, BaseContext.getId());
        this.update(updateWrapper);

        //设置默认地址
        addressBook.setIsDefault(1);
        this.updateById(addressBook);

        return addressBook;
    }
}
