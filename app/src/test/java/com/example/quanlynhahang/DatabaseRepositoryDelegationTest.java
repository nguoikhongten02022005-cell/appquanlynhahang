package com.example.quanlynhahang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class DatabaseRepositoryDelegationTest {

    @Test
    public void databaseHelper_delegatesUserAndDishLogicToDedicatedRepositories() throws Exception {
        String databaseHelperSource = readText(
                "src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java",
                "app/src/main/java/com/example/quanlynhahang/data/DatabaseHelper.java"
        );
        String userRepositorySource = readText(
                "src/main/java/com/example/quanlynhahang/data/UserRepository.java",
                "app/src/main/java/com/example/quanlynhahang/data/UserRepository.java"
        );
        String dishRepositorySource = readText(
                "src/main/java/com/example/quanlynhahang/data/DishRepository.java",
                "app/src/main/java/com/example/quanlynhahang/data/DishRepository.java"
        );

        assertTrue(databaseHelperSource.contains("private final UserRepository userRepository"));
        assertTrue(databaseHelperSource.contains("private final DishRepository dishRepository"));
        assertTrue(databaseHelperSource.contains("return userRepository.getUserById(userId)"));
        assertTrue(databaseHelperSource.contains("return userRepository.getUserByEmail(email)"));
        assertTrue(databaseHelperSource.contains("return userRepository.deleteUser(userId)"));
        assertTrue(databaseHelperSource.contains("return dishRepository.layTatCaMonAn()"));
        assertTrue(databaseHelperSource.contains("return dishRepository.capNhatBanGhiMonAn("));

        assertTrue(userRepositorySource.contains("class UserRepository"));
        assertTrue(userRepositorySource.contains("NguoiDung getUserById(long userId)"));
        assertTrue(userRepositorySource.contains("NguoiDung checkLogin(String usernameOrEmail, String password)"));
        assertTrue(userRepositorySource.contains("List<NguoiDung> getUsersByRole"));
        assertTrue(userRepositorySource.contains("boolean deleteUser(long userId)"));
        assertTrue(userRepositorySource.contains("databaseHelper.getWritableDatabase().delete("));

        assertTrue(dishRepositorySource.contains("class DishRepository"));
        assertTrue(dishRepositorySource.contains("List<DatabaseHelper.DishRecord> layTatCaMonAn()"));
        assertTrue(dishRepositorySource.contains("long themBanGhiMonAn("));
        assertTrue(dishRepositorySource.contains("boolean capNhatBanGhiMonAn("));

        assertFalse(databaseHelperSource.contains("private NguoiDung mapUser(Cursor cursor)"));
        assertFalse(databaseHelperSource.contains("private List<DishRecord> queryDishes("));
        assertFalse(databaseHelperSource.contains("private ContentValues taoGiaTriMonAn("));
    }

    private String readText(String modulePath, String projectPath) throws Exception {
        return new String(Files.readAllBytes(timFile(modulePath, projectPath).toPath()), StandardCharsets.UTF_8);
    }

    private File timFile(String modulePath, String projectPath) {
        File moduleFile = new File(modulePath);
        if (moduleFile.isFile()) {
            return moduleFile;
        }
        File projectFile = new File(projectPath);
        if (projectFile.isFile()) {
            return projectFile;
        }
        fail("Không tìm thấy file: " + projectPath);
        return null;
    }
}
