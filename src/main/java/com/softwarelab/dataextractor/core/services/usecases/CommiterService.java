package com.softwarelab.dataextractor.core.services.usecases;

import com.softwarelab.dataextractor.core.persistence.models.CommiterObject;
import com.softwarelab.dataextractor.core.persistence.models.PagedData;

import java.util.List;

/**
 * Created by Wilson
 * on Thu, 02/09/2021.
 */
public interface CommiterService {
    CommiterObject getCommiter(Long commiterId);
    List<CommiterObject> getProjectCommiters(Long projectId);
    PagedData<CommiterObject> getAllCommiters(int page, int size);
}
