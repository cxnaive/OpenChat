package cn.handyplus.lib.core;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.util.HandyHttpUtil;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.logging.Level;

public final class NetUtil {
   private NetUtil() {
   }

   public static String getLocalMacAddress() {
      Optional<String> macAddress = getMacAddress(getLocalhost());
      return macAddress.orElseGet(HandyHttpUtil::getIp);
   }

   private static Optional<String> getMacAddress(InetAddress inetAddress) {
      return getMacAddress(inetAddress, "-");
   }

   private static Optional<String> getMacAddress(InetAddress inetAddress, String separator) {
      if (null == inetAddress) {
         return Optional.empty();
      } else {
         byte[] mac = getHardwareAddress(inetAddress);
         if (null != mac) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mac.length; i++) {
               if (i != 0) {
                  sb.append(separator);
               }

               String s = Integer.toHexString(mac[i] & 255);
               sb.append(s.length() == 1 ? 0 + s : s);
            }

            return Optional.of(sb.toString());
         } else {
            return Optional.empty();
         }
      }
   }

   private static byte[] getHardwareAddress(InetAddress inetAddress) {
      if (null == inetAddress) {
         return null;
      } else {
         try {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            return null != networkInterface ? networkInterface.getHardwareAddress() : null;
         } catch (SocketException var2) {
            throw new RuntimeException(var2);
         }
      }
   }

   private static InetAddress getLocalhost() {
      LinkedHashSet<InetAddress> localAddressList = localAddressList(address -> !address.isLoopbackAddress() && address instanceof Inet4Address);
      if (CollUtil.isNotEmpty(localAddressList)) {
         InetAddress address2 = null;

         for (InetAddress inetAddress : localAddressList) {
            if (!inetAddress.isSiteLocalAddress()) {
               return inetAddress;
            }

            if (null == address2) {
               address2 = inetAddress;
            }
         }

         if (null != address2) {
            return address2;
         }
      }

      try {
         return InetAddress.getLocalHost();
      } catch (UnknownHostException var4) {
         if (BaseConstants.DEBUG) {
            InitApi.PLUGIN.getLogger().log(Level.SEVERE, "error:", (Throwable)var4);
         }

         return null;
      }
   }

   private static LinkedHashSet<InetAddress> localAddressList(NetUtil.Filter<InetAddress> addressFilter) {
      Enumeration<NetworkInterface> networkInterfaces;
      try {
         networkInterfaces = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException var6) {
         throw new RuntimeException(var6);
      }

      LinkedHashSet<InetAddress> ipSet = new LinkedHashSet<>();

      while (networkInterfaces.hasMoreElements()) {
         NetworkInterface networkInterface = networkInterfaces.nextElement();
         Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

         while (inetAddresses.hasMoreElements()) {
            InetAddress inetAddress = inetAddresses.nextElement();
            if (inetAddress != null && (null == addressFilter || addressFilter.accept(inetAddress))) {
               ipSet.add(inetAddress);
            }
         }
      }

      return ipSet;
   }

   @FunctionalInterface
   private interface Filter<T> {
      boolean accept(T var1);
   }
}
