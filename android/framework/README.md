开发框架及规范说明文档

框架介绍
秒音app安卓端开发框架采用了MVP模式 + Retrofit2 + OKHttp3 + RxJava + Dagger2等技术封装搭建的框架。基础框架包括：sunshine-lib、sunshine-tools、sunshine-net、sunshine-album、sunshine-crop、sunshine-ui 、sunshine-player、sunshine-pay、sunshine-base等模块。

基础模块说明
sunshine-lib，依赖库和JAR管理模块，整个项目的依赖库和jar包导入都在这个模块，任何模块开发都要引入该模块，所以该模块是整个系统最基础的模块。
sunshine-tools，工具类模块，封装了一些常用的工具类。比如ScreenUtils、 StringUtils、
BitmapUtils、GlideUtils、DateUtils、LogUtils等等。
sunshine-net，Http网络请求模块，封装了http请求的post、get、put、delete请求方式的实现，以及http请求方式的文件上传和下载。
sunshine-album，相册功能模块，选择单个，多个图片和视频。
sunshine-crop，图片裁剪功能模块
sunshine-ui，封装了大量的自定义控件，比如下拉刷新，进度条，跑马灯等等常用的自定义控件。
sunshine-player，播放器，底层是ijkplayer开源播放器，在ijkplayer基础上封装，使用更加方便。
sunshine-pay，第三方支付模块，封装了支付宝和微信支付。
sunshine-base，基础模块，封装了最基础的BaseActivity，BaseFragment，BaseApplication，ActivityLifecycleManager Activity的生命周期管理，ScreenAdaptManager屏幕适配。

功能模块划分说明

现在暂时根据功能划分成7个功能模块，home，circle，live，news，shop，user以及common。
home模块：包含了启动页和首页
circle模块:	广场动态，好友圈，附近门店，以及发布动态。
live 模块：主要包含短视频，直播两大功能板块
news 模块：主要包含im通讯，店铺群，普通群，添加好友等功能。
shop 模块：商城模块
user 模块：个人中心
common 模块：共用模块

框架功能调用和配置
1.系统工具类调用。
com.tcj.sunshine.tools.AppUtils 判断是否安装微信，QQ，支付宝，微博
com.tcj.sunshine.tools.BitmapUtils bitmap，byte[]，drawable，InputStream之间的转换。
com.tcj.sunshine.tools.DateUtils 日期格式转换
com.tcj.sunshine.tools.ScreenUtils 得到屏幕宽，屏幕高，状态栏高，底部导航栏高
com.tcj.sunshine.tools.StringUtils 字符串的各种处理方法

2.相册调用
BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG);
config.needCamera(R.mipmap.icon_album_camera).withMaxCount(9); // 支持gif，相机，设置最大选图数
Boxing.of(config).withIntent(context, BoxingActivity.class).start(DemoAlbumActivity.this, 1000);

3.图片裁剪
File targetFile = FileUtils.createFile(FileUtils.getCacheDir(FileUtils.DIR_NAME_TEMP_CACHE),System.currentTimeMillis() + ".jpg");

Uri targetURI = Uri.fromFile(targetFile);
BoxingCropOption cropOption = new BoxingCropOption(targetURI)
        .aspectRatio(1f, 1f)
        .withMaxResultSize(1080, 1080);

BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(R.mipmap.icon_album_camera).withCropOption(cropOption);
Boxing.of(config).withIntent(context, BoxingActivity.class).start(DemoAlbumCropActivity.this, 2000);

4.图片压缩
ImageUtils.withFiles(sourceFiles)
    .setMaxWidth(800)
    .setMaxHeight(800)
    .setQuality(90)
    .setMaxFileSize(200 * 1024)
    .setOptimize(true)
    .build()
    .compress(new ImageUtils.OnCompressCallback<File>() {
        @Override
        public void success(File targetFile, int width, int height) {

        }

        @Override
        public void fail(File source) {

        }

        @Override
        public void complete(List<File> successList, List<File> failList, long millis) {

        }
    });


5.权限请求
PermissionUtils.checkSelfPermission((Activity) context, new String[]{
                        PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE,
                        PermissionUtils.PERMISSION_READ_PHONE_STATE
                }, 1000, new PermissionUtils.PermissionsCallback() {
                    @Override
                    public void callback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == 1000 && permissions.length == grantResults.length) {
                            ToastUtils.show("权限请求成功");
                        }
                    }
                });

6.系统配置
配置文件：com.tcj.sunshine.lib.config.Constants

7.系统sdk配置以及第三方依赖配置
在文件config.gradle

8.打包配置
在：gradle.properties文件中
#打包环境参数
#版本号
APP_VERSION_CODE=1
#版本名称
APP_VERSION=1
#是否是正式环境
HTTP_URL_RELEASE=true
#是否显示日志
IS_SHOW_LOG=true

命名规范
1.Activity的命名，Activity的命名必须要以Activity结尾，如果是列表页最好要以ListActivity结尾，比如：GoodsListActivity；详情页最好要以DetailActivity结尾，比如：OrderDetailActivity，GoodsDetailActivity。
2.Fragment的命名同上。
3.Model类的命名必须要以Model结尾，比如：商品的相关接口的实现可以写在GoodsModel里面。
4.Presenter类命名必须要以Presenter结尾。
5.适配器类以Adapter结尾
6.布局文件的命名，比如Activity的布局文件，模块名_activity_ +  类名小写下划线隔开，如：shop_activity_goods_list, 同理如果是Fragment，则以模块名_fragment开头，窗口的则以模块名_dialog开头，适配器则以模块名_adapter开头，需要动态引入的布局，则以模块名_view开头，比如说：shop_view_goods_detail_top。
7.资源文件命名，可以加上颜色，比如图标：icon_share_white，icon_left_arrow_black。图片则以img开头，比如：img_splash。drawable文件命名：比如：bg_radius_white_3dp
8.布局文件控件id命名，比如：tv_name,iv_cover,相对布局RelativeLayout则为rl_container等等
9.控件在Activity里面命名，采用驼峰法。比如：mTvName,mIvCover, mBtnConfirm
