package com.best.grocery.service;

import android.content.Context;
import android.util.Log;


import com.best.grocery.R;
import com.best.grocery.entity.Category;
import com.best.grocery.entity.PantryList;
import com.best.grocery.entity.Product;
import com.best.grocery.entity.ShoppingList;

import java.util.ArrayList;
import java.util.Date;

public class PantryListService extends GenericService {
    private static final String TAG = PantryListService.class.getSimpleName();
    private CategoryService mCategoryService;
    private Context mContext;

    public PantryListService(Context context) {
        super(context);
        this.mContext = context;
        mCategoryService = new CategoryService(context);
    }

    public PantryList getListActive() {
        PantryList pantryList = mPantryListDao.fetchListActive();
        if (pantryList.getName() == null) {
            Log.d(TAG, "Pantry: init");
            pantryList = new PantryList();
            pantryList.setName(mContext.getResources().getString(R.string.default_name_pantry_list));
            pantryList.setActive(true);
            pantryList.setId(createCodeId(pantryList.getName(), new Date()));
            mPantryListDao.create(pantryList);
            return pantryList;
        }
        return pantryList;
    }


    public void createPantryList(String name) {
        ArrayList<PantryList> list = getAllList();
        for (PantryList pantryList : list) {
            pantryList.setActive(false);
            mPantryListDao.update(pantryList);
        }
        PantryList pantryList = new PantryList();
        pantryList.setName(name);
        pantryList.setActive(true);
        pantryList.setId(createCodeId(pantryList.getName(), new Date()));
        mPantryListDao.create(pantryList);
    }

    public ArrayList<PantryList> getAllList() {
        return mPantryListDao.fetchAll();
    }

    public ArrayList<Product> productPantry(PantryList list) {
        ArrayList<Product> result = new ArrayList<>();
        String query = "select product_user.* from product_user " +
                "inner join category on product_user.id_category = category.id " +
                "where id_pantry_list = '" + list.getId() + "' " +
                "order by category.order_view asc,order_in_group asc";
        ArrayList<Product> productsHaveCategory = mProductDao.findByQuery(query);
        if (productsHaveCategory.size() != 0) {
            Product productFirst = new Product();
            productFirst.setCategory(productsHaveCategory.get(0).getCategory());
            productsHaveCategory.add(0, productFirst);
            int lengh = productsHaveCategory.size();
            for (int i = 1; i < lengh; i++) {
                //Log.d(TAG, "Name product: " + list.get(i).getCategory().getId());
                String lastType = productsHaveCategory.get(i - 1).getCategory().getId();
                String currentType = productsHaveCategory.get(i).getCategory().getId();
                if (!currentType.equals(lastType)) {
                    //Log.d(TAG,"Add header");
                    Product product = new Product();
                    product.setCategory(productsHaveCategory.get(i).getCategory());
                    productsHaveCategory.add(i, product);
                    lengh++;
                }
            }
        }
        String query2 = "select * from product_user " +
                "where id_pantry_list = '" + list.getId() + "' and id_category = '" + DEFAULT_CATEGORY_ID + "' " +
                "order by order_in_group asc";
        ArrayList<Product> productNoCategory = mProductDao.findByQuery(query2);
        if (productNoCategory.size() != 0) {
            //Log.d(TAG, "Loaij khacs");
            Category category = new Category();
            category.setId(DEFAULT_CATEGORY_ID);
            category.setName(mContext.getResources().getString(R.string.default_other_category));
            Product productCategory = new Product();
            productCategory.setCategory(category);
            productNoCategory.add(0, productCategory);
            result.addAll(productNoCategory);
        }
        result.addAll(productsHaveCategory);
        return result;
    }

    public PantryList activeList(String name) {
        PantryList result = new PantryList();
        ArrayList<PantryList> list = getAllList();
        for (PantryList pantryList : list) {
            if (pantryList.getName().equals(name)) {
                pantryList.setActive(true);
                result = pantryList;
            } else {
                pantryList.setActive(false);
            }
            mPantryListDao.update(pantryList);
        }
        return result;
    }

    public void shoppingListToPantryList(String nameList, ArrayList<Product> dataAdd) {
        PantryList pantryList = mPantryListDao.findByName(nameList);
        activeList(nameList);
        for (Product product : dataAdd) {
            if (product.getName() != null) {
                addProductToPantry(product.getName(), pantryList);
            }
        }
    }

    public Product addProductToPantry(String text, PantryList pantryList) {
        Product product = isExitstProduct(text, pantryList);
        if (product == null) {
            Product p = new Product();
            p.setId(createCodeId(text, new Date()));
            p.setName(text);
            p.setPantryList(pantryList);
            Category category = mCategoryService.getCategoryOfNameProduct(text);
            p.setCategory(category);
            p.setState(PRODUCT_FULL);
            orderProductPantryInGroup(p.getCategory(), pantryList);
            mProductDao.create(p);
            return p;
        } else {
            //update product
            product.setState(PRODUCT_FULL);
            product.setExpired(new Date(0));
            product.setCreated(new Date());
            product.setModified(new Date());
            product.setOrderInGroup(0);
            orderProductPantryInGroup(product.getCategory(), pantryList);
            mProductDao.update(product);
            return product;
        }
        //get similar product
    }

    private Product isExitstProduct(String text, PantryList pantryList) {
        ArrayList<Product> products = productPantry(pantryList);
        for (Product p : products) {
            if (p.getName() != null) {
                if (p.getName().equals(text)) return p;
            }
        }
        return null;
    }

    void orderProductPantryInGroup(Category category, PantryList pantryList) {
        String idCategory = category.getId();
        ArrayList<Product> list = mProductDao.getProductByCategoryAndPantry(idCategory, pantryList.getId());
        for (int i = 0; i < list.size(); i++) {
            Product product = list.get(i);
            product.setOrderInGroup(i + 1);
            mProductDao.update(product);
        }
    }


    public void clearAllList(PantryList pantryList) {
        ArrayList<Product> products = mProductDao.findByIdPantry(pantryList.getId());
        for (Product p : products) {
            PantryList pantryListDefault = new PantryList();
            p.setPantryList(pantryListDefault);
            mProductDao.update(p);
        }
    }

    public void deleteList(PantryList pantryList) {
        mPantryListDao.delete(pantryList);
        if(pantryList.isActive()){
            ArrayList<PantryList> list = getAllList();
            if (list.size() != 0) {
                PantryList pantryListActive = list.get(0);
                pantryListActive.setActive(true);
                mPantryListDao.update(pantryListActive);
            }
        }
    }

    public void addProductToShopping(ArrayList<String> listProduct, ShoppingList shoppingList) {
        ShoppingListService shoppingListService = new ShoppingListService(mContext);
        shoppingListService.activeShopping(shoppingList.getName());
        for (String text : listProduct) {
            shoppingListService.addProductToShopping(text, shoppingList);
        }
    }

    public void removeProduct(Product product) {
        PantryList pantryListDefault = new PantryList();
        product.setPantryList(pantryListDefault);
        mProductDao.update(product);
    }

    public void updateList(PantryList pantryList) {
         mPantryListDao.update(pantryList);
    }


}
