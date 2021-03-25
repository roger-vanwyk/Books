package ml.rogervw.roger;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

     public static final Creator<Book> CREATOR = new Creator<Book>() {
          @Override
          public Book createFromParcel(Parcel in) {
               return new Book(in);
          }

          @Override
          public Book[] newArray(int size) {
               return new Book[size];
          }
     };
     String author;
     String title;

     public Book(String author, String title) {
          this.author = author;
          this.title = title;
     }

     protected Book(Parcel in) {
          author = in.readString();
          title = in.readString();
     }

     public String getAuthor() {
          return author;
     }

     public String getTitle() {
          return title;
     }

     @Override
     public void writeToParcel(Parcel parcel, int i) {
          parcel.writeString(author);
          parcel.writeString(title);
     }

     @Override
     public int describeContents() {
          return 0;

     }
}