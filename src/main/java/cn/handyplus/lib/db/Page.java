package cn.handyplus.lib.db;

import java.util.List;
import lombok.Generated;

public class Page<T> {
   private int total;
   private List<T> records;

   public int getTotalPages(int pageSize) {
      return (this.total + pageSize - 1) / pageSize;
   }

   @Generated
   public int getTotal() {
      return this.total;
   }

   @Generated
   public List<T> getRecords() {
      return this.records;
   }

   @Generated
   public void setTotal(int total) {
      this.total = total;
   }

   @Generated
   public void setRecords(List<T> records) {
      this.records = records;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Page)) {
         return false;
      } else {
         Page<?> other = (Page<?>)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.getTotal() != other.getTotal()) {
            return false;
         } else {
            Object this$records = this.getRecords();
            Object other$records = other.getRecords();
            return this$records == null ? other$records == null : this$records.equals(other$records);
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof Page;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getTotal();
      Object $records = this.getRecords();
      return result * 59 + ($records == null ? 43 : $records.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "Page(total=" + this.getTotal() + ", records=" + this.getRecords() + ")";
   }

   @Generated
   public Page(int total, List<T> records) {
      this.total = total;
      this.records = records;
   }
}
