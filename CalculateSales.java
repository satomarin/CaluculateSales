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
		
		
		
		//1.1支店定義ファイルがあるかの確認
		File branchFile = new File(args[0] + File.separator + "branch.lst");
		if (!( branchFile.exists())){
			System.out.println("支店定義ファイルが存在しません");
			return;
		}
		
		
		//1.2　Mapを使用
		HashMap<String, String> branch = new HashMap <String, String>();
		
		
		//3.2　Mapを使用
		//支店-合計金額
		HashMap<String, Long> branchFee = new HashMap <String, Long>(0);
		
		
		//宣言
		BufferedReader branchReader = null;
		
		
		//(例外がありえるからtry必要
		//1.1　表示
		try{
			
			//ファイルを開く(支店定義）(エスケープシーケンス)
			//文字列の受け取り
			branchReader = new BufferedReader(new FileReader (args[0] + File.separator +"branch.lst"));
			
			String branchLine;
			
			//内容がnullじゃなかったら1行ずつ格納　カンマでキーとかを分ける
			//ファイルのフォーマットが不正かの確認
			while((branchLine = branchReader.readLine()) != null){
				String[] bran = branchLine.split(",", 0);
				//3桁の数字
				if(!(bran[0].matches("\\d{3}"))){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				//配列が2個
				if( bran.length != 2){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branch.put(bran[0], bran[1]);
				
				//売上-合計　初期化
				branchFee.put(bran[0], 0l);
			}
			
			
		}catch(IOException e){
			//5.もし何かしらで動かなかった場合の対処
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(branchReader != null){
					branchReader.close();
				}
				
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
		
		
		//商品定義ファイルがあるかの確認
		File commodityFile = new File(args[0]+ File.separator + "commodity.lst");
		if (!commodityFile.exists()){
			System.out.println("商品定義ファイルが存在しません");
			return;
		}
		
		
		//2.2　Mapを使用
		HashMap<String, String>commodity = new HashMap <String, String>();
		
		
		//3.2　Mapを使用
		//商品-合計金額
		HashMap<String, Long>commodityFee = new HashMap <String, Long>();
		
		
		//宣言
		BufferedReader commodityReader = null;
		
		
		//2.1　表示
		try{
			//ファイルを開く(支店定義）(エスケープシーケンス)
			//文字列の受け取り
			commodityReader = new BufferedReader(new FileReader (args[0] + File.separator + "commodity.lst"));

			String commodityLine;

			//内容がnullじゃなかったら1行ずつ格納　カンマでキーとかを分ける
			//ファイルのフォーマットが不正かの確認
			while((commodityLine = commodityReader.readLine()) != null){
				String[] commod = commodityLine.split(",", 0);
				//英数字計8桁
				if(!commod[0] .matches("^[a-zA-Z0-9]{8}$")){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				//配列が2個
				if( commod.length != 2){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodity.put(commod[0], commod[1]);
				
				//商品-合計　初期化
				commodityFee.put(commod[0], 0l);
			}
			
			
		}catch(IOException e){
			
			//もし何かしらで動かなかった場合の対処
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(commodityReader != null){
					commodityReader.close();
				}
				
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
		
		
		
		//3.1
		//ファイル名の取得
		File allName = new File(args[0]);
		File[] allFiles= allName.listFiles();
		
		//List宣言 rcd
		List<File> rcd = new ArrayList<File>();
		
		
		//List宣言 rcdname
		List<String> rcdname = new ArrayList<String>();
		
		
		
		
		
		//ファイル名から.rcdを厳選
		for(int i = 0; i < allFiles.length; i++){
			
			//getNameメソッドを使ってfiles1[]をString型に変更
			//→matchesでファイル名.rcdを厳選 (ファイルであることも)
			if(allFiles[i].getName().matches("\\d{8}"+".rcd") && (allFiles[i].isFile())){
				
				//List:rcdに.rcdファイルを格納
				rcd.add(allFiles[i]);
				
				//List:rcdnameにファイル名の各数字のみを格納
				rcdname.add(allFiles[i].getName().substring(0,8));
				
			}
		}
		
		
		
		//rcdnameを使わないと↓
		//rcd.get(rcd.size() -1).getName().substring(0,8)
		//ファイルが歯抜けになっているか（1からファイルが始まる時のみ稼動）
		//（例えば10からファイル名が始まる時にどうするのかも追加したい）
		
		if(rcdname.size() != Integer.valueOf(rcdname.get(rcdname.size()-1))){
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
			for (int i = 0; i < rcd.size(); i++){
				rcdReader = new BufferedReader( new FileReader (rcd.get(i)));
				
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
		
		
		//4.出力
		//ファイルを作る
		//支店別集計ファイルの作成
		
		FileWriter branchWriter = null;
		
		
		try {
			
			File branchNewfile = new File (args[0] + File.separator +"branch.out");
			//5.
			if (!(branchNewfile.createNewFile())){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			
			//書き込みの宣言
			branchWriter = new FileWriter(args[0] + File.separator +"branch.out");
			
			
			//List作成
			List<Entry<String, Long>> branchEntries = new ArrayList<Entry<String, Long>>(branchFee.entrySet());
			Collections.sort(branchEntries,new Comparator<Entry<String, Long>>() {
				//comparatorで値の比較
				public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
					return o2.getValue().compareTo(o1.getValue());   //降順
				}
			});
			
			//書き込み
			//1つのkey・2つのmapからデータの取り出し
			for(Entry<String, Long> beData : branchEntries){
				branchWriter.write(beData.getKey() + "," + branch.get(beData.getKey()) + "," + beData.getValue() + System.getProperty("line.separator"));
				
			}
			
		}catch(IOException e){
			System.out.println("例外が発生しました");
			return;
		
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(branchWriter != null){
					branchWriter.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
		
		//4.2商品別集計ファイルの作成
		
		FileWriter commodityWriter = null;
		
		
		try {
			
			File commodityNewfile = new File (args[0] + File.separator +"commodity.out");
			//5.
			if (!(commodityNewfile.createNewFile())){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			
			//書き込みの宣言
			commodityWriter = new FileWriter(args[0] + File.separator + "commodity.out");
			
			
			//List作成
			List<Entry<String, Long>> commodityEntries = new ArrayList<Entry<String, Long>>(commodityFee.entrySet());
			Collections.sort(commodityEntries, new Comparator<Entry<String, Long>>() {
				//comparatorで値の比較
				public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
					return o2.getValue().compareTo(o1.getValue());   //降順
				}
			});
			
			//書き込み
			//1つのkey・2つのmapからデータの取り出し
			for(Entry<String, Long> ceData : commodityEntries){
				commodityWriter.write(ceData.getKey() + "," + commodity.get(ceData.getKey()) + "," + ceData.getValue() + System.getProperty("line.separator") );
				
			}
			
		}catch(IOException e){
			
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(commodityWriter != null){
					commodityWriter.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
	}
}

