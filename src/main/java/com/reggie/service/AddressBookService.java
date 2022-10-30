package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {

    //设置默认地址
    AddressBook setDefaultAddr(AddressBook addressBook);
}
