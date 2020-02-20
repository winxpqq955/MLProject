package cn.margele.mlproject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import net.minecraft.client.main.ModifyMain;

public class MCLauncher {
	File mcFile;
	String[] args;

	public MCLauncher(File mcFile, String[] args) {
		this.mcFile = mcFile;
		this.args = args;
	}

	public void launch() {
		try {
			loadJar(mcFile);
			
			ModifyMain.launch(concat(new String[] { "--version", "mcp", "--accessToken", "0", "--assetsDir", "assets",
					"--assetIndex", "1.8", "--userProperties", "{}" }, args));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
    private static void loadJar(File jarFile) throws MalformedURLException {
        if (jarFile.exists() == false) {
            System.out.println("jar file not found.");
            return;
        }

        //获取类加载器的addURL方法，准备动态调用
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
        } 
        
        // 获取方法的访问权限，保存原始值
        boolean accessible = method.isAccessible();
        try {
            //修改访问权限为可写
            if (accessible == false) {
                method.setAccessible(true);
            }

            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            
            //获取jar文件的url路径
            java.net.URL url = jarFile.toURI().toURL();
            
            //jar路径加入到系统url路径里
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //回写访问权限
            method.setAccessible(accessible);
        }
    }

}
