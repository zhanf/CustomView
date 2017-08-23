package zhanf.com.zfcustomview.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import zhanf.com.zfcustomview.R;

/**
 * Created by zhanFeng on 2017/3/22.
 */

public class ImageLoader {

    private static ImageLoader sImageLoader;

    public static ImageLoader getInstance(Context context) {
        if (sImageLoader == null) {
            synchronized (ImageLoader.class) {
                if (sImageLoader == null) {
                    sImageLoader = new ImageLoader(context);
                }
            }
        }
        return sImageLoader;
    }

    private static final String TAG = "ImageLoader";
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    //磁盘缓存空间 20M
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 20;
    private boolean mIsDiskLruCacheCreated = false;

    private final static int TAG_KEY_URI = R.id.imageLoader_uri;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXMUN_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final ThreadFactory sThreadFactor = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    //创建线程池
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXMUN_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), sThreadFactor);

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult loaderResult = (LoaderResult) msg.obj;
            ImageView imageView = loaderResult.mImageView;
            String uri = (String) imageView.getTag(TAG_KEY_URI);
            if (uri.equals(loaderResult.uri)) {
                System.out.println("Handler:" + uri);
                imageView.setImageBitmap(loaderResult.bitmap);
            } else {
                Log.e(TAG, "set image bitmap,but url has changed,ignored!");
            }
        }
    };

    private ImageLoader(Context context) {
        //单例中防止上下文引起的内存泄漏
        mContext = context.getApplicationContext();
        //获取应用内存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        //内存缓存空间为应用内存的1/8
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getWidth() / 1024;
            }
        };

        //创建保存bitmap的文件
        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        //获取手机存储空间大小,当空间允许时创建本地缓存对象
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                //创建成功时设置标记
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取手机存储空间大小
     *
     * @param path
     * @return
     */
    private long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs statFs = new StatFs(path.getPath());
        return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
    }

    /**
     * 创建保存bitmap的文件
     * true 有SD卡且未移除时创建在SD卡创建      false 创建在内置存储卡上
     *
     * @param context
     * @param bitmapName
     * @return
     */
    private File getDiskCacheDir(Context context, String bitmapName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + bitmapName);
    }

    /**
     * 从内存中获取bitmap
     *
     * @param url
     * @return
     */
    private Bitmap loadBitmapFromMemCache(String url) {
        String key = hashKeyFormUrl(url);
        return mMemoryCache.get(key);
    }

    /**
     * 添加bitmap到内存中
     *
     * @param url
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (loadBitmapFromMemCache(url) == null) {
            if (bitmap != null) {
                mMemoryCache.put(url, bitmap);
            }
        }
    }

    public void displayImage(@Nullable final String url, final ImageView imageView) {
        //setTag 与 Handler中getTag可对imageView进行识别，避免异步造成图片显示错位
        imageView.setTag(TAG_KEY_URI, url);

        Bitmap bitmap = loadBitmapFromMemCache(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        //创建任务
        Runnable runnableTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(url, imageView);
                if (bitmap != null) {
                    LoaderResult loaderResult = new LoaderResult(imageView, url, bitmap);
                    //Handler发送消息
                    mMainHandler.obtainMessage(0, loaderResult).sendToTarget();

                }
            }
        };

        //将任务放入线程池执行
        THREAD_POOL_EXECUTOR.execute(runnableTask);
    }

    /**
     * @param url
     * @param imageView
     * @return
     */
    private Bitmap loadBitmap(String url, ImageView imageView) {
        Bitmap bitmap = null;

        bitmap = loadBitmapFromMemCache(url);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            bitmap = loadBitmapFromDiskCache(url, imageView);
            if (bitmap != null) {
                return bitmap;
            }

            bitmap = loadBitmapFromHttp(url, imageView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //存储空间不够大时未创建磁盘缓存，直接从网络加载
        if (bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = downloadBitmapFromUrl(url);
        }

        return bitmap;
    }

    /**
     * 从磁盘中加载bitmap
     *
     * @param url
     * @param imageView
     * @return bitmap
     * @throws IOException
     */
    private Bitmap loadBitmapFromDiskCache(String url, ImageView imageView) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI Thread,it's not recommend!");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        String key = hashKeyFormUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(0);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            ImageSizeUtil.ImageSize imageViewSize = ImageSizeUtil.getImageViewSize(imageView);
            //将文件描述符压缩并转换成bitmap
            bitmap = decodeSampledBitmapFromPath(fileDescriptor, imageViewSize.width, imageViewSize.height);
            if (null != bitmap) {
                //保存到内存中
                addBitmapToMemoryCache(key, bitmap);
            }
        }
        return bitmap;
    }

    private static class LoaderResult {
        private ImageView mImageView;
        private String uri;
        private Bitmap bitmap;

        public LoaderResult(ImageView imageView, String url, Bitmap bitmap) {
            this.mImageView = imageView;
            this.uri = url;
            this.bitmap = bitmap;
        }

    }


    public static final int IO_BUFFER_SIZE = 1024 * 8;

    private Bitmap downloadBitmapFromUrl(String uri) {

        Bitmap bitmap = null;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream is = null;

        try {
            URL url = new URL(uri);
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                is = new BufferedInputStream(httpURLConnection.getInputStream(), IO_BUFFER_SIZE);
                //此处可把bitmap对象进行压缩，可自行实现，很简单的
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Log.e(TAG, "Error in downloadBitmap:" + e);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpURLConnection) {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 从网络下载图片
     *
     * @param url
     * @param imageView
     * @return
     */
    private Bitmap loadBitmapFromHttp(String url, ImageView imageView) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network form UI Thread");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        String key = hashKeyFormUrl(url);
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(key);
            if (null != edit) {
                OutputStream outputStream = edit.newOutputStream(0);
                if (downloadUrlToStream(url, outputStream)) {
                    edit.commit();
                } else {
                    edit.abort();
                }
                mDiskLruCache.flush();
            }
            return loadBitmapFromDiskCache(url, imageView);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从网络加载图片并保存到磁盘中
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * md5加密，避免url存在特殊字符
     *
     * @param url
     * @return
     */
    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] digest) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(0xFF & digest[i]);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    /**
     * 根据图片需要显示的宽和高对图片进行压缩
     *
     * @param fileDescriptor
     * @param width
     * @param height
     * @return
     */
    protected Bitmap decodeSampledBitmapFromPath(FileDescriptor fileDescriptor, int width, int height) {
        // 获得图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        //根据需求的宽和高以及图片实际的宽和高计算SampleSize
        options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
                width, height);

        // 使用获得到的InSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        return bitmap;

    }
}
