package com.getxinfo.content.api;

import com.getxinfo.content.dataaccess.News;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class NewsResourceAssembler {

    private final ModelMapper modelMapper;

    public NewsResourceAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public NewsResource toResource(News news) {
        return modelMapper.map(news, NewsResource.class);
    }

    public News toModel(NewsResource newsResource) {
        News news = modelMapper.map(newsResource, News.class);
        return news;
    }

}
