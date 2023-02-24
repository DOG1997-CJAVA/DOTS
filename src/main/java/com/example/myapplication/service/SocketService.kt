package com.example.myapplication.service

import android.app.Service
import android.os.Looper
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.common.EventMsg
import com.example.myapplication.db.Constants
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.net.*
import java.util.*

class SocketService : Service() {
    /*socket*/
    private var socket: Socket? = null
   // val a:Int = 22;
    /*连接线程*/
    private var connectThread: Thread? = null
    private var timer: Timer? = Timer()
    private var outputStream: OutputStream? = null
    private val socketBinder = SocketBinder()
    private var ip: String? = null
    private var port: String? = null
    private var task: TimerTask? = null

    /*默认不打开重连*/
    private var isReConnect = true
    private val handler = Handler(Looper.getMainLooper())

    //onBind()：在服务没有绑定过任何ServiceConnection时才会调用，
    // 主要是返回服务的IBinder下转类对象给活动，实现活动控制服务
    override fun onBind(intent: Intent): IBinder {
        return socketBinder
    }

    //bindService()：绑定服务，主要是为了让活动与服务之间可以沟通而搭建一条桥梁
    //通过ServiceConnection把Activity中的IBinder下转类（子类）对象 指向 服务中的IBinder下转类实例，
    inner class SocketBinder : Binder() {
        /*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService  */
        val service: SocketService
            get() = this@SocketService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        /*拿到传递过来的ip和端口号*/
        ip = intent.getStringExtra(Constants.INTENT_IP)
        port = intent.getStringExtra(Constants.INTENT_PORT)
        /*初始化socket*/
        initSocket()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun toastMsg(msg: String) {
        handler.post { Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show() }
    }

    fun largenum(num1:Int,num2:Int) = if(num1>num2) num1 else num2;
    /*初始化socket*/
    private fun initSocket() {
        if (socket == null && connectThread == null) {
            connectThread = Thread {
                socket = Socket()
                try {
                    /*超时时间为2秒*/
                    socket!!.connect(InetSocketAddress(ip, port!!.toInt()), 2000)
                    /*连接成功的话  发送心跳包*/if (socket!!.isConnected) {
                        /*因为Toast是要运行在主线程的  这里是子线程  所以需要到主线程哪里去显示toast*/
                        toastMsg(getString(R.string.connect_success_remind))
                        /*发送连接成功的消息*/
                        val msg = EventMsg()
                        for (i in 0..9) {
                            msg.tag = Constants.CONNET_SUCCESS
                            EventBus.getDefault().post(msg)
                            /*发送心跳数据*/
                        }
                        sendBeatData()
                    }
                } catch (e: IOException) {
                    //e.printStackTrace()
                    if (e is SocketTimeoutException) {
                        //toastMsg("连接超时，请检查");
                        val msg = EventMsg()
                        msg.tag = Constants.CONNET_FAIL
                        EventBus.getDefault().post(msg)
                        releaseSocket()
                    } else if (e is NoRouteToHostException) {
                        toastMsg(getString(R.string.connect_error_ip_remind))
                        val msg = EventMsg()
                        msg.tag = Constants.CONNET_FAIL
                        EventBus.getDefault().post(msg)
                        stopSelf()
                    } else if (e is ConnectException) {
                        toastMsg(getString(R.string.connect_error_remind_2))
                        val msg = EventMsg()
                        msg.tag = Constants.CONNET_FAIL
                        EventBus.getDefault().post(msg)
                        stopSelf()
                    }
                }
            }
            /*启动连接线程*/connectThread!!.start()
        }
    }

    /*发送数据*/
    fun sendOrder(order: String) {
        if (socket != null && socket!!.isConnected) {
            /*发送指令*/
            Thread {
                try {
                    outputStream = socket!!.getOutputStream()
                    if (outputStream != null) {
                        //设置输出格式为gbk
                        outputStream!!.write(order.toByteArray(charset("gbk")))
                        outputStream!!.flush()
                    }
                } catch (e: IOException) {
                    //e.printStackTrace()
                }
            }.start()
        } else {
            toastMsg(getString(R.string.connect_error_remind_send))
        }
    }

    /*定时发送心跳数据 保持本客户端连接*/
    private fun sendBeatData() {
        if (timer == null) {
            timer = Timer()
        }
        if (task == null) {
            task = object : TimerTask() {
                override fun run() {
                    try {
                        outputStream = socket!!.getOutputStream()
                        /*这里的编码方式根据你的需求去改*/
                        outputStream!!.write("keep_connect".toByteArray(charset("gbk")))
                        outputStream!!.flush()
                        val msg = EventMsg()
                        msg.tag = Constants.CONNET_SUCCESS
                        EventBus.getDefault().post(msg)
                    } catch (e: Exception) {
                        /*发送失败说明socket断开了或者出现了其他错误*/
                        toastMsg(getString(R.string.connect_error_reconnect))
                        val msg = EventMsg()
                        msg.tag = Constants.CONNET_FAIL
                        EventBus.getDefault().post(msg)
                        /*重连*/releaseSocket()
                        //e.printStackTrace()
                    }
                }
            }
        }
        timer!!.schedule(task, 0, 20000)
    }

    /*释放资源*/
    private fun releaseSocket() {
        if (task != null) {
            task!!.cancel()
            task = null
        }
        if (timer != null) {
            timer!!.purge()
            timer!!.cancel()
            timer = null
        }
        if (outputStream != null) {
            try {
                outputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            outputStream = null
        }
        if (socket != null) {
            try {
                socket!!.close()
            } catch (e: IOException) {
            }
            socket = null
        }
        if (connectThread != null) {
            connectThread = null
        }
        /*若开启自动重连 重新初始化socket*/if (isReConnect) {
            initSocket()
        }
    }

    //onDestroy()：在服务销毁的时候调用
    // 在调用stopService()：停止服务，有可能会触发onDestroy()；
    //stopService()与unbindService()有可能会触发onDestroy()，
    //要触发就必须满足：服务停止，没有ServiceConnection绑定：
    override fun onDestroy() {
        super.onDestroy()
        Log.i("SocketService", "onDestroy")
        toastMsg("onDestroy() 服务销毁")
        isReConnect = false
        releaseSocket()
    }

}