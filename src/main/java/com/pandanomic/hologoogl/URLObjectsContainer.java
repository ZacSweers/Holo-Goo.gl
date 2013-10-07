package com.pandanomic.hologoogl;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by pandanomic on 10/7/13.
 */
@Table(name = "URLObjectsContainer")
public class URLObjectsContainer extends Model {
    @Column(name = "UserName")
    public String userName;

    public List<URLObject> urlObjects() {
        return getMany(URLObject.class, "Container");
    }
}
