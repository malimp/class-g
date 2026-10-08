package com.classapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.graphics.Typeface
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private val students = mutableListOf<Student>()
    private val classes = mutableListOf<SchoolClass>()
    private val daily = mutableListOf<DailyReport>()
    data class Student(val code:String,val first:String,val last:String,val mobile:String,val national:String,val birth:String,val note:String)
    data class SchoolClass(val code:String,val name:String,val level:String,val start:String,val end:String,val days:String,val time:String,val note:String)
    data class DailyReport(val className:String,val date:String,val teacher:String,val text:String)

    override fun onCreate(b: Bundle?) { super.onCreate(b); showHome() }
    private fun base(title:String): LinearLayout { root=LinearLayout(this); root.orientation=LinearLayout.VERTICAL; root.setPadding(28,28,28,28); val t=TextView(this); t.text=title; t.textSize=25f; t.typeface=Typeface.DEFAULT_BOLD; root.addView(t); return root }
    private fun btn(text:String, action:()->Unit)=Button(this).also{it.text=text;it.setOnClickListener{action()}}
    private fun showHome(){
        base("سیستم مدیریت آموزشگاه — کلاس")
        root.addView(btn("👨‍🎓 زبان‌آموزان"){showStudents()}); root.addView(btn("🏫 کلاس‌ها"){showClasses()}); root.addView(btn("📝 گزارش روزانه"){showDaily()}); root.addView(btn("📊 گزارش هفتگی"){showWeekly()}); root.addView(btn("📅 گزارش ماهانه"){showMonthly()}); root.addView(btn("⚙️ تنظیمات / پشتیبان‌گیری"){showSettings()}); setContentView(root)
    }
    private fun back(){showHome()}
    private fun showStudents(){ base("زبان‌آموزان"); root.addView(btn("ورود Excel (.xlsx)"){pick(100)}); root.addView(btn("ثبت دستی"){studentForm()}); val search=EditText(this); search.hint="جستجو بر اساس نام، نام خانوادگی، کد"; root.addView(search); val box=LinearLayout(this);box.orientation=LinearLayout.VERTICAL;root.addView(box); fun render(){box.removeAllViews();students.filter{("${it.code} ${it.first} ${it.last}").contains(search.text,true)}.forEach{box.addView(TextView(this).apply{text="${it.code} — ${it.first} ${it.last}\n${it.mobile}";textSize=17f;padding(10)} )}};search.setOnKeyListener{_,_,_->render();false};render();root.addView(btn("بازگشت"){back()});setContentView(root)}
    private fun studentForm(){ val e=Array(7){EditText(this)}; val labels=listOf("کد زبان‌آموز (خالی = خودکار)","نام","نام خانوادگی","شماره موبایل","کد ملی","تاریخ تولد","توضیحات");base("ثبت زبان‌آموز");labels.forEachIndexed{i,l->e[i].hint=l;root.addView(e[i])};root.addView(btn("ثبت"){students.add(Student(e[0].text.toString().ifBlank{"S${students.size+1}"},e[1].text.toString(),e[2].text.toString(),e[3].text.toString(),e[4].text.toString(),e[5].text.toString(),e[6].text.toString()));showStudents()});root.addView(btn("لغو"){showStudents()});setContentView(root)}
    private fun showClasses(){base("کلاس‌ها");root.addView(btn("ورود Excel (.xlsx)"){pick(101)});root.addView(btn("ثبت دستی"){classForm()});classes.forEach{root.addView(TextView(this).apply{text="${it.code} — ${it.name} | ${it.level}\n${it.days} — ${it.time}";textSize=17f;padding(10)})};root.addView(btn("بازگشت"){back()});setContentView(root)}
    private fun classForm(){val e=Array(8){EditText(this)};val labels=listOf("کد کلاس (خالی = خودکار)","نام کلاس","سطح/دوره","تاریخ شروع","تاریخ پایان","روزهای برگزاری","ساعت","توضیحات");base("ثبت کلاس");labels.forEachIndexed{i,l->e[i].hint=l;root.addView(e[i])};root.addView(btn("ثبت"){classes.add(SchoolClass(e[0].text.toString().ifBlank{"C${classes.size+1}"},e[1].text.toString(),e[2].text.toString(),e[3].text.toString(),e[4].text.toString(),e[5].text.toString(),e[6].text.toString(),e[7].text.toString()));showClasses()});root.addView(btn("لغو"){showClasses()});setContentView(root)}
    private fun showDaily(){base("گزارش روزانه");val classE=EditText(this);classE.hint="نام کلاس";val date=EditText(this);date.hint="تاریخ";val teacher=EditText(this);teacher.hint="نام مدرس";val text=EditText(this);text.hint="متن کامل گزارش روزانه — آزاد و بدون فرم اجباری";text.minLines=12;text.gravity=48;listOf(classE,date,teacher,text).forEach{root.addView(it)};root.addView(btn("ذخیره گزارش"){daily.add(DailyReport(classE.text.toString(),date.text.toString(),teacher.text.toString(),text.text.toString()));Toast.makeText(this,"گزارش ذخیره شد",Toast.LENGTH_SHORT).show()});daily.forEach{root.addView(TextView(this).apply{text="${it.date} — ${it.className}\n${it.text}";padding(10)})};root.addView(btn("بازگشت"){back()});setContentView(root)}
    private fun report(title:String){base(title);val range=EditText(this);range.hint="بازه گزارش (مثلاً 1405/07/01 تا 1405/07/07)";root.addView(range);daily.forEach{root.addView(TextView(this).apply{text="${it.date} — ${it.className}\n${it.text}";padding(10)})};root.addView(EditText(this).apply{hint="توضیحات قابل ویرایش مدرس";minLines=4});root.addView(btn("بازگشت"){back()});setContentView(root)}
    private fun showWeekly()=report("گزارش هفتگی")
    private fun showMonthly()=report("گزارش ماهانه")
    private fun showSettings(){base("تنظیمات");root.addView(TextView(this).apply{text="پشتیبان‌گیری و بازیابی در نسخه بعدی تکمیل می‌شود.\n\nتطبیق گزارش روزانه فقط با نام کوچک انجام می‌شود؛ نام‌های تکراری نیازمند انتخاب دستی هستند و اطلاعاتی که در متن وجود ندارد حدس زده نمی‌شود.";textSize=17f;padding(10)});root.addView(btn("بازگشت"){back()});setContentView(root)}
    private fun pick(code:Int){startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";addCategory(Intent.CATEGORY_OPENABLE)},code)}
    override fun onActivityResult(req:Int,res:Int,data:Intent?){super.onActivityResult(req,res,data);if(res!=RESULT_OK||data?.data==null)return;try{contentResolver.openInputStream(data.data!!)?.use{parseXlsx(it,req)}}catch(e:Exception){Toast.makeText(this,"خطا در خواندن Excel: ${e.message}",Toast.LENGTH_LONG).show()}}
    private fun parseXlsx(input:InputStream, req:Int){val wb=XSSFWorkbook(input);val sh=wb.getSheetAt(0);var added=0;for(r in 1..sh.lastRowNum){val row=sh.getRow(r)?:continue;fun v(i:Int)=row.getCell(i)?.toString()?.trim()? : ""; if(req==100){if(v(1).isNotBlank()) {students.add(Student(v(0).ifBlank{"S${students.size+1}"},v(1),v(2),v(3),v(4),v(5),v(6)));added++}}else{if(v(1).isNotBlank()){classes.add(SchoolClass(v(0).ifBlank{"C${classes.size+1}"},v(1),v(2),v(3),v(4),v(5),v(6),v(7)));added++}}};wb.close();Toast.makeText(this,"$added رکورد وارد شد",Toast.LENGTH_SHORT).show();if(req==100)showStudents() else showClasses()}
    private fun TextView.padding(p:Int){setPadding(p,p,p,p)}
}
