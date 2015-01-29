package com.luwei.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.luwei.adapters.MenuListAdapter;
import com.luwei.domain.UserInfo;
import com.luwei.fragment.HomePager;
import com.luwei.fragment.HomeWeb;
import com.luwei.fragment.LocationModeSourceFragment;
import com.luwei.fragment.NavigationPage;
import com.luwei.fragment.PhotoShare;
import com.luwei.fragment.ScreenLock;
import com.luwei.potato.R;

/**
 * 作为程序的总管理界面，其重要性不言而喻，它关系到用户的体验和优化的能力。 现在这个模式有点模仿网易新闻，或者碗豆荚。好吧一切从模仿开始。
 * 包含2个大的模式，DrawerLayout和复杂的ActionBar MenuItem
 * Fragment采用Astuetz的ActionBar模仿。部分采用ActionBarTab和ViewPager。
 * <p/>
 * App的根界面 控制界面的跳转 包含drawerLayout,actionBar,tab,bottomNavigate
 * 对于抽屉界面来说，需要在DrawerLayout这个组件上和ActionBar交互，
 * DrawerLayout包括FrameView和ListView，FrameView用于替换Fragment ListView用于条目显示
 * Action提供了一个特别的适配器ActionBarDrawerToggle用于回调抽屉的拖出和回进动作。这是
 * ActionBar和DrawerLayout交互的基础，提供了图标，title等交互内容。它被DrawerLayout调用。
 * DrawerLayout图标需要自己set，但图标的点击事件是Menu监听的。调用Android.id.home来对应 icon。
 *
 * @author gpicsdesigner@gmail.com
 *         目前越来越感到MainActivity像一个管理Fragment的容器。而不再像一个内容提供者。
 */
public class FrameActivity extends SherlockFragmentActivity {
    // DrawerLayout部分 的成员变量
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    MenuListAdapter mMenuAdapter;
    String[] title; // 子Fragment的标题
    int[] counter; // 计数，ListView的item上。有什么用呢
    int[] icon; // listView的item 图标
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    Fragment oldFrag;
    FragmentManager fragmentmanager;
    UserInfo profileInfo;
    MenuItem mDefine;

    HomePager homePager;
    PhotoShare photoShare;
    ScreenLock screenLock;
    long exitTime = System.currentTimeMillis();

    // ActionBar部分的成员变量

    // -----------------------------------生命周期区--------------------------------------------//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from drawer_main.xml
        Intent intent = this.getIntent();

        //接受bundle数据，保存到pference而
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            profileInfo = (UserInfo) bundle.getSerializable("UserInfo");
            SharedPreferences setting = getSharedPreferences("profile", 0);
            SharedPreferences.Editor editor = setting.edit();
            editor.putString("userId", profileInfo.getPreId());
            editor.putString("nickName", profileInfo.getNick_name());
            editor.putString("avatarUrl", profileInfo.getAvatar_url());
            editor.putString("city", profileInfo.getCity());
            editor.putString("province", profileInfo.getProvince());
            editor.commit();
        }

        setContentView(R.layout.drawer_main);
        init();
    }


    // -----------------------------------初始化UI以及控制逻辑------------------------------------//

    private void init() {
        /**
         * DrawerLayout 部分
         */
        fragmentmanager = getSupportFragmentManager();
        /**
         * init Fragment
         */
        HomeWeb homeWeb = new HomeWeb();
        homePager = new HomePager();
        photoShare = new PhotoShare();
        screenLock = new ScreenLock();
        // 开始toggle视图的配置 Get the Title
        mTitle = mDrawerTitle = getTitle();

        // Generate title
        title = new String[]{"简介", "", "科技前沿", "图片墙", "定位方位", "", "选购商品",
                "售后服务"};

        // Generate subtitle
        counter = new int[]{0, 0, 0, 01, 0, 0, 01, 0};

        // Generate icon
        icon = new int[]{0, 0, R.drawable.ic_home, R.drawable.ic_photos,
                R.drawable.ic_location, 0, R.drawable.ic_commodity,
                R.drawable.ic_services};

        // Locate DrawerLayout in drawer_main.xml
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Locate ListView in drawer_main.xml
        mDrawerList = (ListView) findViewById(R.id.listview_drawer);

        // Set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        // Pass string arrays to MenuListAdapter
        if (profileInfo == null) {
            mMenuAdapter = new MenuListAdapter(FrameActivity.this, title,
                    counter, icon, null);
        } else {
            mMenuAdapter = new MenuListAdapter(FrameActivity.this, title,
                    counter, icon, profileInfo);
        }

        // Set the MenuListAdapter to the ListView
        mDrawerList.setAdapter(mMenuAdapter);

        // Capture listview menu item click
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        // 一个目的，让ActionBar的App图标响应DrawerLayout
        getSupportActionBar().setIcon(R.drawable.ic_menu_l);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer_n, // icon
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                // Set the title on the action when drawer open
                getSupportActionBar().setTitle(mDrawerTitle);
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
        oldFrag = homeWeb;
        fragmentTransaction.replace(R.id.content_frame, oldFrag);
        fragmentTransaction.attach(oldFrag);
        fragmentTransaction.commit();

    }

    // -----------------------------------子控制逻辑方法区-------------------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Used to put dark icons on light action bar
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        // searchView.setIconifiedByDefault(false); //默认方式打开，不是图标化
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        mDefine = menu.findItem(R.id.action_refresh);
        return true;
    }

    /**
     * ActionBar的Menu监听
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View mView;
        ImageView iv;
        if (item.getItemId() == android.R.id.home) {

            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case android.R.id.home:
                mDefine.collapseActionView();
                mDefine.setActionView(null);
                return true;

            case R.id.action_search:
                return true;

            case R.id.action_change_arrow:
                item.setIcon(R.drawable.menuclick);
                return true;

            case R.id.action_refresh:
                mDefine.setActionView(R.layout.menu_task);
                mDefine.expandActionView();
                mView = mDefine.getActionView();
                mView.setBackgroundColor(getResources().getColor(R.color.blue_task));
                iv = (ImageView) mView.findViewById(R.id.firstIv);
                iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.out.println("icon 点击事件");
                    }
                });
                return true;


            case R.id.menu_navigation:
                mDefine.setActionView(R.layout.meun_navigation);
                mDefine.expandActionView();
                mView = mDefine.getActionView();
                mView.setBackgroundColor(getResources().getColor(R.color.purple));
                mDrawerLayout.closeDrawers();
                changeFragment(new LocationModeSourceFragment());
                iv = (ImageView) mView.findViewById(R.id.firstIv);
                iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    }
                });
                return true;

            case R.id.menu_talking:
                mDefine.setActionView(R.layout.menu_talking);
                mDefine.expandActionView();
                mView = mDefine.getActionView();
                mView.setBackgroundColor(getResources().getColor(R.color.yellow));
                iv = (ImageView) mView.findViewById(R.id.firstIv);
                iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.out.println("icon 点击事件");
                    }
                });
                return true;

            case R.id.menu_task:
                mDefine.setActionView(R.layout.menu_task);
                mDefine.expandActionView();
                mView = mDefine.getActionView();
                mView.setBackgroundColor(getResources().getColor(R.color.blue_task));
                iv = (ImageView) mView.findViewById(R.id.firstIv);
                iv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        System.out.println("icon 点击事件");
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Drawer内部listView的item，点击事件监听，注意是LISTVIEW
     */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position, view);
        }
    }

    /**
     * 配合上面的点击事件监听更换对应的fragment视图
     *
     * @param position
     */
    private void selectItem(int position, View view) {
        // FragmentTransaction fragmentTransation = getSupportFragmentManager()
        // .beginTransaction();
        // 0去那了，0用来当profile了。
        if (position == 1 || position == 0 || position == 5) {
        } else {
            switch (position) {
                case 2:
                    changeFragment(homePager);
                    break;
                case 3:
                    changeFragment(photoShare);
                    break;
                case 4:
                    changeFragment(new NavigationPage());
                    break;
                case 6:
                    changeFragment(screenLock);
                    break;
                case 7:
                    changeFragment(photoShare);
                    break;
            }
            mDrawerList.setItemChecked(position, true);
            setTitle(title[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    /**
     * 由于drawerlayoutitem 在跳转时候出现fragment与底部导航
     * 的fragment使用了不同的transaction，导致attach和detach 出现bug，所以用这个方法来避免问题，只是目前我用的是同一个
     * transaction所以这个方法暂时备用。
     *
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        if (fragment != fragmentmanager.findFragmentById(
                R.id.content_frame)) {
            if (oldFrag != null) {
                FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
                fragmentTransaction.detach(oldFrag);
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.attach(fragment);
                fragmentTransaction.commit();
                oldFrag = fragment;
            }
        }
    }

    // -----------------------------------Activity状态更改调整区-------------------------------------------//

    /**
     * 更换actionBar的图标
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * 同步状态，比如横屏与竖屏的切换。
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * 由于手机状态的改变，比如横屏与竖屏的切换，会改变configuration的 变化，而当前的Actionbar会回调该对象，故此需要重新加载。
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * 首先FragmentFactory作为管理fragment替换工作的类，调用极其频繁
     * 但在离开当前activity时候应当进行销毁。所以简单的置空，千万不要 将其中方法做为静态方法，又不销毁类，那样会出现再次进入App而这个
     * 类还存在的情况，导致程序bug。
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 退出当前activity,FragmentManager 的堆操作
     */
    @Override
    public void onBackPressed() {

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            // If there are back-stack entries, leave the FragmentActivity
            // implementation take care of them.
            manager.popBackStack();

        } else {
            super.onBackPressed();
        }
    }

    /**
     * 按2次键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT)
                        .show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }

            return false;
        } else {
            return true;
        }
    }

}
