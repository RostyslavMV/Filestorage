package com.rmv.filestorage.repository;

import com.rmv.filestorage.model.File;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FileRepository extends ElasticsearchRepository<File,String> {
}
