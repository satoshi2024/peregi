package com.peregi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peregi.entity.AddressBook;
import com.peregi.mapper.AddressBookMapper;
import com.peregi.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
