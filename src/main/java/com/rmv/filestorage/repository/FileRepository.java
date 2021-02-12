package com.rmv.filestorage.repository;

import com.rmv.filestorage.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Set;


public interface FileRepository extends ElasticsearchRepository<File,String> {

    Page<File> findAllByTags(Set<String> tags, Pageable pageable);
}
