import { View, Text, StyleSheet } from 'react-native'
import React from 'react'

const MusicPlayer = () => {
  return (
    <View style = {style.container}>
      <Text>Music Player</Text>
    </View>
  )
}

export default MusicPlayer;

const style = StyleSheet.create({
    container: {
        flex: 1,
    },
});