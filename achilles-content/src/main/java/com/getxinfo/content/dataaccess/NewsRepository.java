package com.getxinfo.content.dataaccess;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface NewsRepository extends PagingAndSortingRepository<News, Long> {

    @Transactional
    @Modifying
    @Query("update News u set u.title = :title, u.content = :content where u.id = :id")
    void updateNews(@Param(value = "id") long id,
                    @Param(value = "title") String title,
                    @Param(value = "content") String content);


}
