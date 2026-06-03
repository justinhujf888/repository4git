package com.weavict.common.ejb

import com.fasterxml.jackson.databind.ObjectMapper

import java.text.SimpleDateFormat

class StaticBean
{
    static ObjectMapper objectMapper;

    static ObjectMapper objectMapperDateTime;

    static ObjectMapper buildObjectMapper()
    {
        if (objectMapper==null)
        {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    static ObjectMapper objectMapping4DateTime(String dateFormat,String timeZone)
    {
        if (objectMapperDateTime==null)
        {
            objectMapperDateTime = new ObjectMapper();
        }
        if (!(dateFormat in [null,""]))
        {
            objectMapperDateTime.setDateFormat(new SimpleDateFormat(dateFormat));
        }
        else
        {
            objectMapperDateTime.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }

        if (!(timeZone in [null,""]))
        {
            objectMapperDateTime.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        else
        {
            objectMapperDateTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        }

        return objectMapperDateTime;
    }
}
