package lazier.org.testdemo

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_rx.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import android.icu.text.AlphabeticIndex.Record
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Predicate
import java.util.concurrent.TimeUnit
import org.reactivestreams.Subscription
import org.reactivestreams.Subscriber
import io.reactivex.BackpressureStrategy
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.Flowable
import java.io.*


/**
 * Created by :TYK
 * Date: 2019/7/16  16:10
 * Desc:  测试RX 相关
 */
class RxActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "RxActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx)
        tv_rx1.setOnClickListener(this)
        Log.e(TAG, Thread.currentThread().name)
    }

    fun rxObservable() {
        //创建一个上游 Observable：
        val observable = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            emitter.onNext(1)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onComplete()
        })

        //创建一个下游 Observer
        val observer = object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.e(TAG, "subscribe")
            }

            override fun onNext(t: Int) {
                Log.e(TAG, t.toString())
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "error")
            }

            override fun onComplete() {
                Log.e(TAG, "onComplete")
            }
        }
        observable.subscribe(observer)


    }


    /**
     * Disposable   防止当前页面关闭的时候 就关闭接收 以免造镇崩溃                 CompositeDisposable().clear()
     * CompositeDisposable().add(Disposable)
     *
     */
    fun test1() {
        Observable.create(ObservableOnSubscribe<Int> { emitter ->
            Log.e(TAG, "emit1")
            emitter.onNext(1)
            Log.e(TAG, "emit2")
            emitter.onNext(2)
            Log.e(TAG, "emit3")
            emitter.onNext(3)
            emitter.onComplete()
            Log.e(TAG, "emit4")
            emitter.onNext(4)
        }).subscribe(object : Observer<Int> {
            var disposable: Disposable? = null
            var i = 0
            override fun onSubscribe(d: Disposable) {
                Log.e(TAG, "subscribe")
                disposable = d
            }

            override fun onNext(t: Int) {
                Log.e(TAG, t.toString())
                i++
                if (i == 2) {
                    Log.e(TAG, "dispose")
                    disposable?.dispose()
                    Log.e(TAG, "isDisposed : " + disposable?.isDisposed)
                }
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "error")
            }

            override fun onComplete() {
                Log.e(TAG, "onComplete")
            }
        })

    }

    /**
     * thread
     * 线程操作  比如耗时操作 这些放在子线程 发送 在主线程接收（二）
     */
    @SuppressLint("CheckResult")
    fun test2() {
        val observable = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            Log.e(TAG, "Observable thread is : " + Thread.currentThread().name)
            Log.e(TAG, "emit 1")
            emitter.onNext(1)
        })
        val consumer = Consumer<Int> {
            Log.e(TAG, "Observer thread is :" + Thread.currentThread().name)
            Log.e(TAG, "onNext: $it")
        }

        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    Log.e(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().name)
                }
                .observeOn(Schedulers.io())
                .doOnNext {
                    Log.e(TAG, "After observeOn(io), current thread is: " + Thread.currentThread().name)
                }
                .subscribe(consumer)
    }


    /**
     * map
     * 一般用于两个接口相关性用  比如 注册后就马上登陆 （三）
     */
    @SuppressLint("CheckResult")
    fun test3() {
        Observable.create(ObservableOnSubscribe<Int> { emitter ->
            emitter.onNext(1)
            emitter.onNext(2)
            emitter.onNext(3)
        }).concatMap(object : io.reactivex.functions.Function<Int, ObservableSource<String>> {
            override fun apply(t: Int): ObservableSource<String> {
                val list = arrayListOf<String>()
                for (i in 0..2) {
                    list.add("I am value $t")
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS)
            }
        }).subscribe(Consumer<String> { s -> Log.e(TAG, s) })


    }

    /**
     * Zip
     *  一般用在需要从两个接口中分别拿到数据后  在操作相关数据（四）
     */
    @SuppressLint("CheckResult")
    fun test4() {
        //创建一个上游 Observable：
        val observable1 = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            Log.e(TAG, "emit 1")
            emitter.onNext(1)
            Thread.sleep(1000)
            Log.e(TAG, "emit 2")
            emitter.onNext(2)
            Thread.sleep(1000)

            Log.e(TAG, "emit 3")
            emitter.onNext(3)
            Thread.sleep(1000)

            Log.e(TAG, "emit 4")
            emitter.onNext(4)

            Log.e(TAG, "emit onComplete1")
            emitter.onComplete()
        }).subscribeOn(Schedulers.io())
        val observable2 = Observable.create(ObservableOnSubscribe<String> { emitter ->
            Log.e(TAG, "emit A")
            emitter.onNext("A")
            Thread.sleep(1000)

            Log.e(TAG, "emit B")
            emitter.onNext("B")
            Thread.sleep(1000)

            Log.e(TAG, "emit C")
            emitter.onNext("C")
            Thread.sleep(1000)

            Log.e(TAG, "emit onComplete2")
            emitter.onComplete()
        }).subscribeOn(Schedulers.io())


        Observable.zip(observable1, observable2, object : BiFunction<Int, String, String> {
            override fun apply(t1: Int, t2: String): String {
                return (t1 * 100).toString() + t2
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.e(TAG, "subscribe")
            }

            override fun onNext(t: String) {
                Log.e(TAG, t)
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "error")
            }

            override fun onComplete() {
                Log.e(TAG, "onComplete")
            }
        })
    }

    /**（六）
     * Flowable
     * filter  过滤
     * sample  操作符, 简单做个介绍, 这个操作符每隔指定的时间就从上游中取出一个事件发送给下游  （用于每隔时间段取一次数据 比如取K线什么的）
     */
    @SuppressLint("CheckResult")
    fun test56() {
        //创建一个上游 Observable：
        val observable1 = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            var i = 0
            while (true) {   //无限循环发事件
                emitter.onNext(i)
                i++
            }
        }).subscribeOn(Schedulers.io()).filter(object : Predicate<Int> {
            override fun test(t: Int): Boolean {
                return t % 10 == 0
            }
        })
        val observable2 = Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("A")
        }).subscribeOn(Schedulers.io()).sample(2, TimeUnit.SECONDS)


        Observable.zip(observable1, observable2, object : BiFunction<Int, String, String> {
            override fun apply(t1: Int, t2: String): String {
                return (t1 * 100).toString() + t2
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Consumer<String> {
            override fun accept(t: String?) {
                Log.e(TAG, t)
            }

        }, Consumer<Throwable> {
            Log.e(TAG, it.toString())
        })
    }

    /**（七八）
     * Flowable 响应式拉取
     * Flowable  （类似于Observable）（观察者）  Subscriber （类似于Observer）(被观察者)
     */
    fun test7() {
        val upstream = Flowable.create(FlowableOnSubscribe<Int> { emitter ->
            Log.e(TAG, "emit 1")
            emitter.onNext(1)
            Log.e(TAG, "emit 2")
            emitter.onNext(2)
            Log.e(TAG, "emit 3")
            emitter.onNext(3)
            Log.e(TAG, "emit complete")
            emitter.onComplete()
        }, BackpressureStrategy.ERROR) //增加了一个参数  策略 （当上游发射很多事件的时候） 可以抛异常也可以给个缓存
        //, Drop就是直接把存不下的事件丢弃,Latest就是只保留最新的事件，ERROR抛异常，buffer，MISSING给出信号

        val downstream = object : Subscriber<Int> {

            override fun onSubscribe(s: Subscription) {
                Log.e(TAG, "onSubscribe")
                s.request(java.lang.Long.MAX_VALUE)  //注意这句代码
                //这句是下游能处理几个上游发送的数据  否则就抛出BackpressureStrategy.ERROR 上面这个异常
            }

            override fun onNext(integer: Int?) {
                Log.e(TAG, "onNext: " + integer!!)

            }

            override fun onError(t: Throwable) {
                Log.e(TAG, "onError: ", t)
            }

            override fun onComplete() {
                Log.e(TAG, "onComplete")
            }
        }

        upstream.subscribe(downstream)
    }

    var mSubscription: Subscription? = null
    /**
     * 读文件
     */
    fun test9() {
        Flowable.create(object : FlowableOnSubscribe<String> {
            override fun subscribe(emitter: FlowableEmitter<String>) {
                Log.e(TAG, "11111")
                try {
//                    val file = File("D:\\test.txt")
//                    val reader = FileReader(file)
                    var inputStreamReader: InputStreamReader? = null
                    var inputStream: InputStream? = null
                    inputStream = resources.openRawResource(R.raw.test)
                    try {
                        inputStreamReader = InputStreamReader(inputStream, "UTF-8")

                    } catch (e1: UnsupportedEncodingException) {
                        e1.printStackTrace()
                    }
                    val br = BufferedReader(inputStreamReader)

                    val str = br.readLine()
                    emitter.onNext(str)
                    while (br.readLine() != null && !emitter.isCancelled) {
                        while (emitter.requested() == 0L) {
                            if (emitter.isCancelled) {
                                break
                            }
                        }
                        emitter.onNext(br.readLine())
                    }

                    br.close()
//                    reader.close()

                    emitter.onComplete()
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }

        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(
                        object : FlowableSubscriber<String> {
                            override fun onComplete() {
                                Log.e(TAG, "onComplete")

                            }

                            override fun onSubscribe(s: Subscription) {
                                mSubscription = s
                                s.request(1)
                            }

                            override fun onNext(t: String?) {
                                Log.e(TAG, t)
                                try {
                                    Thread.sleep(2000)
                                    mSubscription!!.request(1)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }

                            }

                            override fun onError(t: Throwable?) {
                                Log.e(TAG, t.toString())

                            }

                        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_rx1 -> {
//                rxObservable()
//                test1()
                test9()
                try {
                    Thread.sleep(10000000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
    }

}