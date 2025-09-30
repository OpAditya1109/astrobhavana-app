import React, { useRef, useEffect, useState } from "react";
import { StyleSheet, SafeAreaView, BackHandler } from "react-native";
import { WebView } from "react-native-webview";

export default function HomeScreen() {
  const webViewRef = useRef<WebView>(null);
  const [canGoBack, setCanGoBack] = useState(false);

  useEffect(() => {
    const backAction = () => {
      if (canGoBack && webViewRef.current) {
        webViewRef.current.goBack();
        return true; // ✅ navigate back in WebView
      }
      BackHandler.exitApp(); // ✅ exit app if no history
      return true;
    };

    const backHandler = BackHandler.addEventListener(
      "hardwareBackPress",
      backAction
    );

    return () => backHandler.remove();
  }, [canGoBack]);

  return (
    <SafeAreaView style={styles.container}>
      <WebView
        ref={webViewRef}
        source={{ uri: "https://astrobhavana.com" }}
        startInLoadingState={true}
        javaScriptEnabled={true}
        domStorageEnabled={true}
        scalesPageToFit={true}
        showsVerticalScrollIndicator={false}
        showsHorizontalScrollIndicator={false}
        pullToRefreshEnabled={true}
        onNavigationStateChange={(navState) => setCanGoBack(navState.canGoBack)}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1, // ✅ no need for paddingTop, SafeAreaView handles it
  },
});
