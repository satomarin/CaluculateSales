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
		if(args[0] == null){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		
		
		//1.1支店定義ファイルがあるかの確認
		File file = new File(args[0] + File.separator + "branch.lst");
		if (!( file.exists())){
			System.out.println("支店定義ファイルが存在しません。");
			return;
		}
		
		
		//1.2　Mapを使用
		HashMap<String, String> branch = new HashMap <String, String>();
		
		
		//3.2　Mapを使用
		//支店-合計金額
		HashMap<String, Long> branchrevenue = new HashMap <String, Long>(0);
		
		
		//宣言
		BufferedReader bh = null;
		
		
		//(例外がありえるからtry必要
		//1.1　表示
		try{
			
			//ファイルを開く(支店定義）(エスケープシーケンス)
			//文字列の受け取り
			bh = new BufferedReader(new FileReader (args[0] + File.separator +"branch.lst"));
			
			String b;
			
			//内容がnullじゃなかったら1行ずつ格納　カンマでキーとかを分ける
			//ファイルのフォーマットが不正かの確認
			while((b = bh.readLine()) != null){
				String[] bran = b.split(",", 0);
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
				branchrevenue.put(bran[0], 0l);
			}
			
			
		}catch(IOException e){
			//5.もし何かしらで動かなかった場合の対処
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(bh != null){
					bh.close();
				}
				
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
		
		
		//商品定義ファイルがあるかの確認
		File file1 = new File(args[0]+ File.separator + "commodity.lst");
		if (!file1.exists()){
			System.out.println("商品定義ファイルが存在しません。");
			return;
		}
		
		
		//2.2　Mapを使用
		HashMap<String, String>commodity = new HashMap <String, String>();
		
		
		//3.2　Mapを使用
		//商品-合計金額
		HashMap<String, Long>productrevenue = new HashMap <String, Long>();
		
		
		//宣言
		BufferedReader cd = null;
		
		
		//2.1　表示
		try{
			//ファイルを開く(支店定義）(エスケープシーケンス)
			//文字列の受け取り
			cd = new BufferedReader(new FileReader (args[0] + File.separator + "commodity.lst"));

			String c;

			//内容がnullじゃなかったら1行ずつ格納　カンマでキーとかを分ける
			//ファイルのフォーマットが不正かの確認
			while((c = cd.readLine()) != null){
				String[] commod = c.split(",", 0);
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
				productrevenue.put(commod[0], 0l);
			}
			
			
		}catch(IOException e){
			
			//もし何かしらで動かなかった場合の対処
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(cd != null){
					cd.close();
				}
				
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
		
		
		
		//3.1
		//ファイル名の取得
		File file3 = new File(args[0]);
		File[] files1= file3.listFiles();
		
		//List宣言 rcd
		List<File> rcd = new ArrayList<File>();
		
		
		//List宣言 rcdname
		List<String> rcdname = new ArrayList<String>();
		
		
		
		
		
		//ファイル名から.rcdを厳選
		for(int i = 0; i < files1.length; i++){
			
			//getNameメソッドを使ってfiles1[]をString型に変更
			//→matchesでファイル名.rcdを厳選
			if(files1[i].getName().matches("\\d{8}"+".rcd")){
				
				//List:rcdに.rcdファイルを格納
				rcd.add(files1[i]);
				
				//List:rcdnameにファイル名の拡数字のみを格納
				rcdname.add(files1[i].getName().substring(0,8));
				
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
		String productcode = null;//商品コード
		long revenue1 = 0;//各-合計金額
		long brevenue = 0;//支店-合計金額
		long prevenue = 0;//商品-合計金額
		
		
		String r;
		BufferedReader rd = null;
		
		try{
			
			//表示のためのfor
			for (int i = 0; i < rcd.size(); i++){
				rd = new BufferedReader( new FileReader (rcd.get(i)));
				
				//1ファイルずつのリストの作成
				List <String> datafile = new ArrayList<String> ();
				
				//1行ずつリストに格納
				while((r = rd.readLine()) != null){
					datafile.add(r);
				}
				
				if(datafile.size() != 3){
					System.out.println("売上ファイルのフォーマットが不正です");
					return;
				}
				
				
				//各種データを各変数に格納
				branchcode = datafile.get(0);
				productcode = datafile.get(1);
				revenue1 = Long.parseLong(datafile.get(2));
				
				
				//上記データと1/2の定義ファイルのmapを比較、各項目がちゃんとあるか確認
				if (!(branch.containsKey(branchcode))){
					System.out.println("売上ファイルの支店コードが不正です");
					return;
				}
				if (!(commodity.containsKey(productcode))){
					System.out.println("売上ファイルの商品コードが不正です");
					return;
				}
				
				
				//合計を計算
				long brankari = branchrevenue.get(branchcode);
				brevenue = brankari + revenue1;
				
				long prekari = productrevenue.get(productcode);
				prevenue = prekari + revenue1;
				
				
				if((brevenue >= 9999999999.9) || (prevenue >= 9999999999.9)){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				
				//mapに入力
				branchrevenue.put (branchcode , brevenue);
				productrevenue.put (productcode , prevenue);
				
				
				
			}
		
		
		}catch(IOException e){
			
			//もし何かしらで動かなかった場合の対処
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(rd != null){
					rd.close();
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
		
		FileWriter fw = null;
		
		
		try {
			
			File newfile = new File (args[0] + File.separator +"branch.out");
			//5.
			if (!(newfile.createNewFile())){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			
			//書き込みの宣言
			fw = new FileWriter(args[0] + File.separator +"branch.out");
			
			
			//List作成
			List<Entry<String, Long>> entries = new ArrayList<Entry<String, Long>>(branchrevenue.entrySet());
			Collections.sort(entries,new Comparator<Entry<String, Long>>() {
				//comparatorで値の比較
				public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
					return o2.getValue().compareTo(o1.getValue());   //降順
				}
			});
			
			//書き込み
			//1つのkey・2つのmapからデータの取り出し
			for(Entry<String, Long> bar : entries){
				fw.write(bar.getKey() + "," + branch.get(bar.getKey()) + "," + bar.getValue() + System.getProperty("line.separator"));
				
			}
			
		}catch(IOException e){
			System.out.println("例外が発生しました");
			return;
		
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(fw != null){
					fw.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
		
		//4.2商品別集計ファイルの作成
		
		FileWriter fw1 = null;
		
		
		try {
			
			File newfile1 = new File (args[0] + File.separator +"commodity.out");
			//5.
			if (!(newfile1.createNewFile())){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
			
			//書き込みの宣言
			fw1 = new FileWriter(args[0] + File.separator + "commodity.out");
			
			
			//List作成
			List<Entry<String, Long>> entries1 = new ArrayList<Entry<String, Long>>(productrevenue.entrySet());
			Collections.sort(entries1, new Comparator<Entry<String, Long>>() {
				//comparatorで値の比較
				public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
					return o2.getValue().compareTo(o1.getValue());   //降順
				}
			});
			
			//書き込み
			//1つのkey・2つのmapからデータの取り出し
			for(Entry<String, Long> ber : entries1){
				fw1.write(ber.getKey() + "," + commodity.get(ber.getKey()) + "," + ber.getValue() + System.getProperty("line.separator") );
				
			}
			
		}catch(IOException e){
			
			System.out.println("予期せぬエラーが発生しました");
			return;
			
		}finally{
			
			//内容がnullになったらストリームを閉じる　必ず必要
			try {
				if(fw1 != null){
					fw1.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}
		
	}
}

