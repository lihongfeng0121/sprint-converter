package com.sprint.common.converter.conversion.nested.converters;

import com.sprint.common.converter.conversion.nested.NestedConverter;
import com.sprint.common.converter.conversion.nested.NestedConverterLoader;
import com.sprint.common.converter.exception.ConversionException;
import com.sprint.common.converter.util.Miscs;
import com.sprint.common.converter.util.TypeDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author hongfeng.li
 * @since 2023/3/1
 */
public class SpecificBeanConverters implements NestedConverterLoader {

    public static class BlobToByteArray implements NestedConverter {

        @Override
        public boolean support(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return Blob.class.isAssignableFrom(sourceType.getActualClass()) && targetType.isArray();
        }

        @Override
        public Object convert(Object sourceValue, TypeDescriptor targetTypeDescriptor) throws ConversionException {
            Blob source = (Blob) sourceValue;
            if (source == null) {
                return null;
            }

            InputStream blobStream = null;
            try {

                blobStream = source.getBinaryStream();

                if (blobStream != null) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Miscs.copy(blobStream, baos);
                    return baos.toByteArray();
                }

            } catch (SQLException e) {
                throw new ConversionException("Couldn't retrieve data from blob.", e);
            } catch (IOException e) {
                throw new ConversionException("Couldn't retrieve data from blob.", e);
            } finally {
                if (blobStream != null) {
                    try {
                        blobStream.close();
                    } catch (IOException e) {
                        throw new ConversionException("Couldn't close binary stream for given blob.", e);
                    }
                }
            }

            return null;
        }
    }
}
