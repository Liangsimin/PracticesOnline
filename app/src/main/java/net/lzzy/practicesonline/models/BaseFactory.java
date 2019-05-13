package net.lzzy.practicesonline.models;


import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;
import net.lzzy.sqllib.Sqlitable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public  class BaseFactory<T extends Sqlitable> {
    private static final BaseFactory ourInstance = new BaseFactory();
    protected SqlRepository<T> repository;
    private final Class<T> tClass;

    public static BaseFactory getInstance() {
        return ourInstance;
    }
    public BaseFactory() {
        tClass = getSuperClassGenricType(getClass(), 0);
        repository=new SqlRepository<>(AppUtils.getContext(), tClass, DbConstants.packager);

    }

    /*public List<T> get(){
        return repository.get();
    }

    public T getById(String id){
        return repository.getById(id);
    }*/

    /**
     * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     *
     *@param clazz
     *            clazz The class to introspect
     * @param index
     *            the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     *         determined
     */
    public  Class getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }
}
