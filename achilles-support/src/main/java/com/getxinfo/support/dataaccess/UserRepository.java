package com.getxinfo.support.dataaccess;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByTelphone(String telphone);

}
