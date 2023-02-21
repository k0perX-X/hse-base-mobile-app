package com.example.hsebase.DataBase;

//import android.arch.lifecycle.LiveData;

import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Date;
import java.util.List;

@Dao
public interface HseDao {
    @Query("SELECT * FROM `group`")
    LiveData<List<GroupEntity>> getAllGroup();

    @Insert
    void insertGroup(List<GroupEntity> data);

    @Delete
    void delete(GroupEntity data);

    @Query("SELECT * FROM `teacher`")
    LiveData<List<TeacherEntity>> getAllTeacher();

    @Insert
    void insertTeacher(List<TeacherEntity> data);

    @Delete
    void delete(TeacherEntity data);

    @Query("SELECT * FROM time_table")
    LiveData<List<TimeTableEntity>> getAllTimeTable();

    @Transaction
    @Query("SELECT * FROM time_table " +
            "WHERE :date <= time_start AND :nextDate >= time_start and group_id = :groupId " +
            "ORDER BY time_start LIMIT 100")
    LiveData<List<TimeTableWithTeacherEntity>> getTimeTableTeacherByDateAndGroup(Date date,
                                                                                 Date nextDate,
                                                                                 int groupId);

    @Transaction
    @Query("SELECT * FROM time_table " +
            "WHERE time_start <= :currentTime AND time_end >= :currentTime and group_id = :groupId " +
            "ORDER BY time_start Limit 1 ")
    LiveData<List<TimeTableWithTeacherEntity>> getTimeTableTeacherCurrentByGroup(Date currentTime,
//                                                                                 Date nextDay,
                                                                                 int groupId);

    @Insert
    void insertTimeTable(List<TimeTableEntity> data);
}