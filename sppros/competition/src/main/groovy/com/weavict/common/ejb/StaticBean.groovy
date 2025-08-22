package com.weavict.common.ejb

import com.fasterxml.jackson.databind.ObjectMapper

class StaticBean
{
    static ObjectMapper objectMapper;

    static ObjectMapper buildObjectMapper()
    {
        if (objectMapper==null)
        {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }
}
