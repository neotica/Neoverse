//
//  ContentView.swift
//  alexandria
//
//  Created by Ryo Martin on 17/06/25.
//

import SwiftUI
import AlexandriaApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea(.all)
    }
}

#Preview {
     ContentView()
}
