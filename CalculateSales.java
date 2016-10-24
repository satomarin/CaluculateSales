package jp.co.iccom.sato_marin.CalculateSales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
public class CalculateSales {
	
	public static boolean fileRead(String path, HashMap<String, String> contents, HashMap<String, Long> fee, String name, String conditions){
		
		//宣言
		BufferedReader reader = null;
		
		//支店定義ファイルがあるかの確認
		File definitionFile = new File(path);
		if (!definitionFile.exists()){
			System.out.println(name + "定義ファイルが存在しません");
			return false;
		}
		
		//(例外がありえるからtry必要
		//1.1　表示
		try{
			
			//ファイルを開く(支店定義）(エスケープシーケンス)
			//文字列の受け取り
			reader = new BufferedReader(new FileReader (path));
			
			String line;
			
			//内容がnullじゃなかったら1行ずつ格納　カンマでキーとかを分ける
			//ファイルのフォーマットが不正かの確認
			while((line = reader.readLine()) != null){
				String[] lineContent = line.split(",", 0);
				//3桁の数字
				if(!(lineContent[0].matches(conditions))){
					System.out.println(name + "定義ファイルのフォーマットが不正です");
					return false;
				}
				//配列が2個
				if( lineContent.length != 2){
					System.out.println(name + "定義ファイルのフォーマットが不正です");
					return false;
				}
				contents.put(lineContent[0], lineContent[1]);
				
				//売上-合計　初期化
				fee.put(lineContent[0], 0l);
			}
			
		}catch(IOException e){
			
			//5.もし何かしらで動かなかった場合の対処
			System.out.println("予期せぬエラーが発生しました");
			return false;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(reader != null){
					reader.close();
				}
				
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		
		return true;
		
	}
	
	public static void main(String[]args) {
		
		//5.コマンドライン引数が定義されていない
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		
		//5.args[0]があるかないか
		File argsFile = new File(args[0]);
		if (!( argsFile.exists())){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		
		//1.2　Mapを使用
		HashMap <String, String> branch = new HashMap <String, String>();
		
		//3.2　Mapを使用
		//支店-合計金額
		HashMap <String, Long> branchFee = new HashMap <String, Long>();
		
		
		//2.2　Mapを使用
		HashMap <String, String> commodity = new HashMap <String, String>();
		
		//3.2　Mapを使用
		//商品-合計金額
		HashMap <String, Long> commodityFee = new HashMap <String, Long>();
		
		//宣言
		String branchInPath = (args[0] + File.separator + "branch.lst");
		String branchName = ("支店");
		String branchConditions = ("\\d{3}");
		
		String commondityInPath = (args[0] + File.separator + "commodity.lst");
		String commodifyName = ("商品");
		String commodityConditions = ("^[a-zA-Z0-9]{8}$");
		
		if(!fileRead(branchInPath, branch, branchFee, branchName, branchConditions)){
			return;
		}
		
		if(!fileRead(commondityInPath, commodity, commodityFee, commodifyName, commodityConditions)){
			return;
		}
		
		//3.1
		//ファイル名の取得
		File allName = new File(args[0]);
		File[] allFiles = allName.listFiles();
		
		//List宣言 rcd
		List<File> rceFiles = new ArrayList<File>();
		
		//List宣言 rcdname
		List<String> rcdNames = new ArrayList<String>();
		
		//ファイル名から.rcdを厳選
		for(int i = 0; i < allFiles.length; i++){
			
			//getNameメソッドを使ってfiles1[]をString型に変更
			//→matchesでファイル名.rcdを厳選 (ファイルであることも)
			if(allFiles[i].getName().matches("\\d{8}"+".rcd") && (allFiles[i].isFile())){
				
				//List:rcdに.rcdファイルを格納
				rceFiles.add(allFiles[i]);
				
				//List:rcdnameにファイル名の各数字のみを格納
				rcdNames.add(allFiles[i].getName().substring(0,8));
				
			}
		}
		
		//rcdnameを使わないと↓
		//rcd.get(rcd.size() -1).getName().substring(0,8)
		//ファイルが歯抜けになっているか（1からファイルが始まる時のみ稼動）
		//（例えば10からファイル名が始まる時にどうするのかも追加したい）
		
		if(rcdNames.size() != Integer.valueOf(rcdNames.get(rcdNames.size()-1))){
			//if(rcds.isDirectory)
			System.out.println("売上ファイル名が連番になっていません");
			return;
		}
		
		//3.2
		//各情報の取り出し　list
		
		String branchcode = null;//支店コード
		String commoditycode = null;//商品コード
		long totalFee = 0;//各-合計金額
		long branchTotal = 0;//支店-合計金額
		long commodityTotal = 0;//商品-合計金額
		
		String rcdLine;
		BufferedReader rcdReader = null;
		
		try{
			
			//表示のためのfor
			for (int i = 0; i < rceFiles.size(); i++){
				rcdReader = new BufferedReader( new FileReader (rceFiles.get(i)));
				
				//1ファイルずつのリストの作成
				List <String> eachList = new ArrayList<String> ();
				
				//1行ずつリストに格納
				while((rcdLine = rcdReader.readLine()) != null){
					eachList.add(rcdLine);
				}
				
				if(eachList.size() != 3){
					System.out.println(allFiles[i].getName() + "のフォーマットが不正です");
					return;
				}
				
				//各種データを各変数に格納
				branchcode = eachList.get(0);
				commoditycode = eachList.get(1);
				totalFee = Long.parseLong(eachList.get(2));
				
				//上記データと1/2の定義ファイルのmapを比較、各項目がちゃんとあるか確認
				if (!(branch.containsKey(branchcode))){
					System.out.println(allFiles[i].getName() + "の支店コードが不正です");
					return;
				}
				
				if (!(commodity.containsKey(commoditycode))){
					System.out.println(allFiles[i].getName() + "の商品コードが不正です");
					return;
				}
				
				//合計を計算
				long branchPrice = branchFee.get(branchcode);
				branchTotal = branchPrice + totalFee;
				
				long commodityPrice = commodityFee.get(commoditycode);
				commodityTotal = commodityPrice + totalFee;
				
				if((branchTotal >= 9999999999l) || (commodityTotal >= 9999999999l)){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				
				//mapに入力
				branchFee.put (branchcode , branchTotal);
				commodityFee.put (commoditycode , commodityTotal);
				
			}
			
		}catch(IOException | NumberFormatException e){
			
			//もし何かしらで動かなかった場合の対処
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				
				if(rcdReader != null){
					rcdReader.close();
				}
				
			} catch (IOException e) {
				
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
				
			}
		}
		
		//宣言
		String branchOutPath = (args[0] + File.separator + "branch.out");
		String commodityOutPath = (args[0] + File.separator + "commodity.out");
		
		if (!fileWrite(branchOutPath, branchFee, branch)){
			return;
		}
		
		if(!fileWrite(commodityOutPath, commodityFee, commodity)){
			return;
		}
		
	}
	
	public static boolean fileWrite (String path, HashMap <String,Long> fee, HashMap <String,String> contents){
		
		//4.出力
		//ファイルを作る
		//支店別集計ファイル・商品別集計ファイルの作成
		
		FileWriter writer = null;
		
		try {
			
			//書き込みの宣言
			writer = new FileWriter(path);
			
			//List作成
			List<Entry<String, Long>> entries = new ArrayList<Entry<String, Long>>(fee.entrySet());
			Collections.sort(entries, new Comparator<Entry<String, Long>>() {
				//comparatorで値の比較
				public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
					return o2.getValue().compareTo(o1.getValue());   //降順
				}
			});
			
			//書き込み
			//1つのkey・2つのmapからデータの取り出し
			for(Entry<String, Long> data : entries){
				writer.write(data.getKey() + "," + contents.get(data.getKey()) + "," + data.getValue() + System.getProperty("line.separator"));
				
			}
			
		}catch(IOException e){
			System.out.println(path + "予期せぬエラーが発生しました");
			return false;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				
				if(writer != null){
					writer.close();
				}
				
			} catch (IOException e) {
				
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return false;
				
			}
		}
		return true;
		
	}

}
