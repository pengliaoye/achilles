package com.getxinfo.content.business;

import com.getxinfo.content.dataaccess.News;
import com.getxinfo.content.dataaccess.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final ReactiveElasticsearchClient elasticsearchClient;

    public Flux<Map<String, Object>> findAll() {
        SearchRequest searchRequest = new SearchRequest("rdbms_sync_idx");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("title", "aaa"));
//        searchRequest.source(sourceBuilder);
        Flux<SearchHit> searchHitFlux = elasticsearchClient.search(searchRequest);
        return searchHitFlux.map(SearchHit::getSourceAsMap);
    }

    public Mono<News> create(Mono<News> news) {
        return news.flatMap(t -> Mono.just(newsRepository.save(t)));
    }

    public Mono<News> update(Mono<News> news, Long id) {
        return news.flatMap(t -> {
            t.setId(id);
            return Mono.just(newsRepository.save(t));
//            newsRepository.updateNews(id, t.getTitle(), t.getContent());
//            Optional<News> dbNews = newsRepository.findById(id);
//            return Mono.justOrEmpty(dbNews);
        });
    }

    public Mono<News> findById(Long id) {
        return Mono.justOrEmpty(newsRepository.findById(id));
    }

    public Mono<Void> deleteById(Long id) {
        newsRepository.deleteById(id);
        DeleteRequest deleteRequest = new DeleteRequest("rdbms_sync_idx", String.valueOf(id));
        return elasticsearchClient.delete(deleteRequest).then();
    }

}
