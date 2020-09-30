package io.qtalk.qgamejsinterfacetester.helpers

enum class QTalkTestUsers(
    val userIdRemote: String,
    val userName: String,
    val displayName: String,
    val avatarUrl: String
) {
    TEST_USER_1(
        "5cab25cf1c9d440000308a34",
        "QTalkTest1",
        "QTalk Test 1",
        "https://quiph-assets.s3.ap-south-1.amazonaws.com/avatars/pngs/drawable-xhdpi/1F468-200D-1F3ED.png"
    ),
    TEST_USER_2(
        "5cab26c11c9d440000308a35",
        "QTalkTest2",
        "QTalk Test 2",
        "https://quiph-assets.s3.ap-south-1.amazonaws.com/avatars/pngs/drawable-xhdpi/1F308.png"
    ),
    TEST_USER_3(
        "5cab26e51c9d440000308a36",
        "QTalkTest3",
        "QTalk Test 3",
        "https://quiph-assets.s3.ap-south-1.amazonaws.com/avatars/pngs/drawable-xhdpi/1F353.png"
    )
}